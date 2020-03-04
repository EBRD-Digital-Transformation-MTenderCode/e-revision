package com.procurement.revision.infrastructure.repository

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.procurement.revision.application.repository.AmendmentRepository
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.functional.Result.Companion.failure
import com.procurement.revision.domain.functional.Result.Companion.success
import com.procurement.revision.domain.functional.asSuccess
import com.procurement.revision.domain.functional.bind
import com.procurement.revision.domain.model.amendment.Amendment
import com.procurement.revision.domain.model.amendment.AmendmentId
import com.procurement.revision.infrastructure.fail.Fail
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
        private const val columnOcid = "ocid"
        private const val columnId = "id"
        private const val columnData = "data"

        private const val SAVE_NEW_AMENDMENT = """
               INSERT INTO $keySpace.$tableName(
                      $columnCpid,
                      $columnOcid,
                      $columnId,
                      $columnData
               )
               VALUES(?, ?, ?, ?)
               IF NOT EXISTS
            """

        private const val FIND_BY_CPID_AND_OCID_CQL = """
               SELECT $columnData
                 FROM $keySpace.$tableName
                WHERE $columnCpid=? 
                  AND $columnOcid=?
            """

        private const val FIND_BY_CPID_AND_OCID_AND_ID_CQL = """
               SELECT $columnData
                 FROM $keySpace.$tableName
                WHERE $columnCpid=? 
                  AND $columnOcid=?
                  AND $columnId=?
            """
    }

    private val preparedFindByCpidAndOcidCQL = session.prepare(FIND_BY_CPID_AND_OCID_CQL)
    private val preparedFindByCpidAndOcidAndIdCQL = session.prepare(FIND_BY_CPID_AND_OCID_AND_ID_CQL)
    private val preparedSaveNewAmendmentCQL = session.prepare(SAVE_NEW_AMENDMENT)

    override fun findBy(cpid: String, ocid: String): Result<List<Amendment>, Fail> {
        val query = preparedFindByCpidAndOcidCQL.bind()
            .apply {
                setString(columnCpid, cpid)
                setString(columnOcid, ocid)
            }

        return load(query)
            .doOnError { error -> return failure(error) }
            .get
            .map { row -> converter(row = row) }
            .asSuccess()
    }

    override fun findBy(cpid: String, ocid: String, id: AmendmentId): Result<Amendment?, Fail> {
        val query = preparedFindByCpidAndOcidAndIdCQL.bind()
            .apply {
                setString(columnCpid, cpid)
                setString(columnOcid, ocid)
                setUUID(columnId, id)
            }

        return load(query)
            .doOnError { error -> return failure(error) }
            .get
            .one()
            ?.let { row -> converter(row = row) }
            .asSuccess()
    }

    private fun load(statement: BoundStatement): Result<ResultSet, Fail.Incident.DatabaseInteractionIncident> = try {
        success(session.execute(statement))
    } catch (expected: Exception) {
        failure(Fail.Incident.DatabaseInteractionIncident(expected))
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

    override fun saveNewAmendment(cpid: String, ocid: String, amendment: Amendment): Result<Boolean, Fail> {
        val entity = convert(amendment)
        val statements = preparedSaveNewAmendmentCQL.bind()
            .apply {
                setString(columnCpid, cpid)
                setString(columnOcid, ocid)
                setUUID(columnId, amendment.id)
                setString(columnData, entity.toJson())
            }

        return load(statements).bind { resultSet ->
            success(resultSet.wasApplied())
        }
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
