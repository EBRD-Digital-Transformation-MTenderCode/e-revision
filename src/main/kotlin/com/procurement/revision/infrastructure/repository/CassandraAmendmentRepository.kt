package com.procurement.revision.infrastructure.repository

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.procurement.revision.infrastructure.exception.DatabaseInteractionException
import com.procurement.revision.application.repository.AmendmentRepository
import com.procurement.revision.domain.model.Amendment
import com.procurement.revision.domain.model.AmendmentId
import com.procurement.revision.infrastructure.model.entity.AmendmentDataEntity
import com.procurement.revision.infrastructure.utils.toJson
import com.procurement.revision.infrastructure.utils.toObject
import org.springframework.stereotype.Repository

@Repository
class CassandraAmendmentRepository(private val session: Session) : AmendmentRepository {

    companion object {
        private const val keySpace = "revision"
        private const val tableName = "amendments"
        private const val columnCpid = "cpid"
        private const val columnId = "id"
        private const val columnData = "data"

        private const val SAVE_NEW_AMENDMENT = """
               INSERT INTO $keySpace.$tableName(
                      $columnCpid,
                      $columnId,
                      $columnData
               )
               VALUES(?, ?, ?)
               IF NOT EXISTS
            """

        private const val FIND_BY_CPID_CQL = """
               SELECT $columnCpid,
                      $columnData
                 FROM $keySpace.$tableName
                WHERE $columnCpid=?
            """

        private const val FIND_BY_CPID_AND_ID_CQL = """
               SELECT $columnCpid,
                      $columnId,
                      $columnData
                 FROM $keySpace.$tableName
                WHERE $columnCpid=? 
                  AND $columnId=?
            """
    }

    private val preparedFindByCpidCQL = session.prepare(FIND_BY_CPID_CQL)
    private val preparedFindByCpidAndIdCQL = session.prepare(FIND_BY_CPID_AND_ID_CQL)
    private val preparedSaveNewAmendmentCQL = session.prepare(SAVE_NEW_AMENDMENT)

    override fun findBy(cpid: String): List<Amendment> {
        val query = preparedFindByCpidCQL.bind()
            .apply {
                setString(columnCpid, cpid)
            }

        val resultSet = load(query)
        return resultSet.map { row ->
            converter(row = row)
        }
    }

    override fun findBy(cpid: String, id: AmendmentId): Amendment? {
        val query = preparedFindByCpidAndIdCQL.bind()
            .apply {
                setString(columnCpid, cpid)
                setUUID(columnId, id)
            }

        val resultSet = load(query)
        return resultSet.one()
            ?.let { row ->
                converter(row = row)
            }
    }

    private fun load(statement: BoundStatement): ResultSet = try {
        session.execute(statement)
    } catch (expected: Exception) {
        throw DatabaseInteractionException(
            message = "Error read Amendments(s) from the database.",
            cause = expected
        )
    }

    private fun converter(row: Row): Amendment {
        val entity = row.getString(columnData)
            .toObject(AmendmentDataEntity::class.java)

        val token = entity.token
        val owner = entity.owner
        return entity.amendment.let { amendment ->
            Amendment(
                id = amendment.id,
                token = token,
                owner = owner,
                description = amendment.description,
                rationale = amendment.rationale,
                relatesTo = amendment.relatesTo,
                relatedItem = amendment.relatedItem,
                status = amendment.status,
                type = amendment.type,
                date = amendment.date,
                documents = amendment.documents
                    ?.map { document ->
                        Amendment.Document(
                            id = document.id,
                            documentType = document.documentType,
                            description = document.description,
                            title = document.title
                        )
                    }
                    .orEmpty()
            )
        }
    }

    override fun saveNewAmendment(cpid: String, amendment: Amendment): Boolean {
        val entity = convert(amendment)
        val statements = preparedSaveNewAmendmentCQL.bind()
            .apply {
                setString(columnCpid, cpid)
                setUUID(columnId, amendment.id)
                setString(columnData, entity.toJson())
            }

        return saveAmendment(statements).wasApplied()
    }

    private fun saveAmendment(statement: BoundStatement): ResultSet = try {
        session.execute(statement)
    } catch (expected: Exception) {
        throw DatabaseInteractionException(
            message = "Error writing Amendments.",
            cause = expected
        )
    }

    fun convert(amendment: Amendment) = AmendmentDataEntity(
        token = amendment.token,
        owner = amendment.owner,
        amendment = AmendmentDataEntity.AmendmentEntity(
            id = amendment.id,
            description = amendment.description,
            rationale = amendment.rationale,
            relatesTo = amendment.relatesTo,
            relatedItem = amendment.relatedItem,
            status = amendment.status,
            type = amendment.type,
            date = amendment.date,
            documents = amendment.documents
                .map { document ->
                    AmendmentDataEntity.AmendmentEntity.Document(
                        id = document.id,
                        documentType = document.documentType,
                        description = document.description,
                        title = document.title
                    )
                }
        )
    )
}
