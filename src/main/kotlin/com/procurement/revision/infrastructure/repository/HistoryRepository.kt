package com.procurement.revision.infrastructure.repository

import com.datastax.driver.core.Session
import com.procurement.revision.infrastructure.model.entity.HistoryEntity
import com.procurement.revision.infrastructure.utils.localNowUTC
import com.procurement.revision.infrastructure.utils.toDate
import com.procurement.revision.infrastructure.utils.toJson
import org.springframework.stereotype.Service

@Service
class HistoryRepository(private val session: Session) {

    companion object {
        private const val KEYSPACE = "revision"
        private const val HISTORY_TABLE = "history"
        private const val OPERATION_ID = "operation_id"
        private const val COMMAND = "command"
        private const val OPERATION_DATE = "operation_date"
        private const val JSON_DATA = "json_data"

        private const val SAVE_HISTORY_CQL = """
               INSERT INTO $KEYSPACE.$HISTORY_TABLE(
                      $OPERATION_ID,
                      $COMMAND,
                      $OPERATION_DATE
                      $JSON_DATA
               )
               VALUES(?, ?, ?,?)
               IF NOT EXISTS
            """

        private const val FIND_HISTORY_ENTRY_CQL = """
               SELECT $OPERATION_ID,
                      $COMMAND,
                      $OPERATION_DATE
                      $JSON_DATA
                 FROM $KEYSPACE.$HISTORY_TABLE
                WHERE $OPERATION_ID=?
                  AND $COMMAND=?
               LIMIT 1
            """
    }

    private val preparedSaveHistoryCQL = session.prepare(SAVE_HISTORY_CQL)
    private val preparedFindHistoryByCpidAndCommandCQL = session.prepare(FIND_HISTORY_ENTRY_CQL)

    fun getHistory(operationId: String, command: String): HistoryEntity? {
        val query = preparedFindHistoryByCpidAndCommandCQL.bind().apply {
            setString(OPERATION_ID, operationId)
            setString(COMMAND, command)
        }
        val row = session.execute(query).one()
        return if (row != null) HistoryEntity(
            row.getString(OPERATION_ID),
            row.getString(COMMAND),
            row.getTimestamp(OPERATION_DATE),
            row.getString(JSON_DATA)
        ) else null
    }

    fun saveHistory(operationId: String, command: String, result: Any): HistoryEntity {
        val entity = HistoryEntity(
            operationId = operationId,
            command = command,
            operationDate = localNowUTC().toDate(),
            jsonData = result.toJson()
        )

        val insert = preparedSaveHistoryCQL.bind().apply {
            setString(OPERATION_ID, entity.operationId)
            setString(COMMAND, entity.command)
            setTimestamp(OPERATION_DATE, entity.operationDate)
            setString(JSON_DATA, entity.jsonData)
        }
        session.execute(insert)
        return entity
    }
}
