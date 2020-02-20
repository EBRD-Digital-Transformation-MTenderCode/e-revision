package com.procurement.revision.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.revision.infrastructure.repository.HistoryRepository
import com.procurement.revision.infrastructure.utils.toJson
import com.procurement.revision.infrastructure.utils.toObject
import com.procurement.revision.infrastructure.web.dto.Action
import com.procurement.revision.infrastructure.web.dto.ApiResponse2
import com.procurement.revision.infrastructure.web.dto.ApiSuccessResponse2
import com.procurement.revision.infrastructure.web.dto.getId
import com.procurement.revision.infrastructure.web.dto.getVersion
import org.slf4j.LoggerFactory

abstract class AbstractHistoricalHandler<ACTION : Action, R : Any>(
    private val target: Class<R>,
    private val historyRepository: HistoryRepository
) : Handler<ACTION, ApiResponse2> {
    companion object {
        private val log = LoggerFactory.getLogger(AbstractHistoricalHandler::class.java)
    }

    override fun handle(node: JsonNode): ApiResponse2 {
        val id = node.getId()
        val version = node.getVersion()

        val history = historyRepository.getHistory(id.toString(), action.value)
        if (history != null) {
            val result = history.jsonData.toObject(target)
            return ApiSuccessResponse2(version = version, id = id, result = result)
        }
        val result = execute(node)
        historyRepository.saveHistory(id.toString(), action.value, result)
        if (log.isDebugEnabled)
            log.debug("${action.value} has been executed. Result: ${result.toJson()}")

        return ApiSuccessResponse2(version = version, id = id, result = result)
    }

    abstract fun execute(node: JsonNode): R
}

