package com.procurement.revision.infrastructure.repository

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Session
import com.procurement.revision.application.repository.HistoryRepository
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.functional.asSuccess
import com.procurement.revision.infrastructure.fail.Fail
import com.procurement.revision.infrastructure.model.entity.HistoryEntity
import com.procurement.revision.infrastructure.utils.localNowUTC
import com.procurement.revision.infrastructure.utils.toDate
import com.procurement.revision.infrastructure.utils.toJson
import org.springframework.stereotype.Repository

@Repository
class HistoryRepositoryCassandra(private val session: Session) : HistoryRepository {

    companion object {
        private const val KEYSPACE = "revision"
        private const val HISTORY_TABLE = "history"
        private const val COMMAND_ID = "command_id"
        private const val COMMAND = "command"
        private const val COMMAND_DATE = "command_date"
        private const val JSON_DATA = "json_data"

        private const val SAVE_HISTORY_CQL = """
               INSERT INTO $KEYSPACE.$HISTORY_TABLE(
                      $COMMAND_ID,
                      $COMMAND,
                      $COMMAND_DATE,
                      $JSON_DATA
               )
               VALUES(?, ?, ?, ?)
               IF NOT EXISTS
            """

        private const val FIND_HISTORY_ENTRY_CQL = """
               SELECT $COMMAND_ID,
                      $COMMAND,
                      $COMMAND_DATE,
                      $JSON_DATA
                 FROM $KEYSPACE.$HISTORY_TABLE
                WHERE $COMMAND_ID=?
                  AND $COMMAND=?
               LIMIT 1
            """
    }

    private val preparedSaveHistoryCQL = session.prepare(SAVE_HISTORY_CQL)
    private val preparedFindHistoryByCpidAndCommandCQL = session.prepare(FIND_HISTORY_ENTRY_CQL)

    override fun getHistory(operationId: String, command: String): Result<HistoryEntity?, Fail> {
        val query = preparedFindHistoryByCpidAndCommandCQL.bind()
            .apply {
                setString(COMMAND_ID, operationId)
                setString(COMMAND, command)
            }

        return load(query)
            .doOnError { error -> return Result.failure(error) }
            .get
            .one()
            ?.let { row ->
                HistoryEntity(
                    row.getString(COMMAND_ID),
                    row.getString(COMMAND),
                    row.getTimestamp(COMMAND_DATE),
                    row.getString(JSON_DATA)
                )
            }
            .asSuccess()
    }

    override fun saveHistory(operationId: String, command: String, result: Any): Result<HistoryEntity, Fail> {
        val entity = HistoryEntity(
            operationId = operationId,
            command = command,
            operationDate = localNowUTC().toDate(),
            jsonData = result.toJson()
        )

        val insert = preparedSaveHistoryCQL.bind()
            .apply {
                setString(COMMAND_ID, entity.operationId)
                setString(COMMAND, entity.command)
                setTimestamp(COMMAND_DATE, entity.operationDate)
                setString(JSON_DATA, entity.jsonData)
            }

        load(insert).doOnError { error -> return Result.failure(error) }

        return entity.asSuccess()
    }

    private fun load(statement: BoundStatement): Result<ResultSet, Fail.Incident.DatabaseInteractionIncident> = try {
        Result.success(session.execute(statement))
    } catch (expected: Exception) {
        Result.failure(Fail.Incident.DatabaseInteractionIncident(expected))
    }
}
