package com.procurement.revision.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.revision.infrastructure.utils.toJson
import com.procurement.revision.infrastructure.web.dto.Action
import com.procurement.revision.infrastructure.web.dto.ApiResponse
import com.procurement.revision.infrastructure.web.dto.ApiSuccessResponse
import com.procurement.revision.infrastructure.web.dto.getId
import com.procurement.revision.infrastructure.web.dto.getVersion
import org.slf4j.LoggerFactory

abstract class AbstractHandler<ACTION : Action, R : Any> : Handler<ACTION, ApiResponse> {
    companion object {
        private val log = LoggerFactory.getLogger(AbstractHandler::class.java)
    }

    override fun handle(node: JsonNode): ApiResponse {
        val id = node.getId()
        val version = node.getVersion()

        val result = execute(node)

        if (log.isDebugEnabled)
            log.debug("${action.value} has been executed. Result: ${result?.toJson()}")
        return ApiSuccessResponse(version = version, id = id, result = result)
    }

    abstract fun execute(node: JsonNode): R?
}