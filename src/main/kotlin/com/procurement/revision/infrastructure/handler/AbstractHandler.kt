package com.procurement.revision.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.infrastructure.fail.Fail
import com.procurement.revision.infrastructure.utils.toJson
import com.procurement.revision.infrastructure.web.dto.Action
import com.procurement.revision.infrastructure.web.dto.ApiResponse
import com.procurement.revision.infrastructure.web.dto.ApiSuccessResponse
import com.procurement.revision.infrastructure.web.dto.generateResponseOnFailure
import com.procurement.revision.infrastructure.web.dto.tryGetId
import com.procurement.revision.infrastructure.web.dto.tryGetVersion
import org.slf4j.LoggerFactory

abstract class AbstractHandler<ACTION : Action, R : Any> : Handler<ACTION, ApiResponse> {
    companion object {
        private val log = LoggerFactory.getLogger(AbstractHandler::class.java)
    }

    override fun handle(node: JsonNode): ApiResponse {
        val id = node.tryGetId().get
        val version = node.tryGetVersion().get

        return when (val result = execute(node)) {
            is Result.Success -> {
                if (log.isDebugEnabled)
                    log.debug("${action.key} has been executed. Result: ${result.get.toJson()}")
                return ApiSuccessResponse(version = version, id = id, result = result.get)
            }
            is Result.Failure -> generateResponseOnFailure(result.error, version, id)
        }
    }

    abstract fun execute(node: JsonNode): Result<R, List<Fail>>
}