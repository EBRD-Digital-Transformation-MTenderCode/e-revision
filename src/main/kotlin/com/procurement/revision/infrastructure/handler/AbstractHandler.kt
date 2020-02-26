package com.procurement.revision.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.revision.domain.util.Result
import com.procurement.revision.infrastructure.handler.validation.ValidationError
import com.procurement.revision.infrastructure.utils.toJson
import com.procurement.revision.infrastructure.web.dto.Action
import com.procurement.revision.infrastructure.web.dto.ApiFailResponse
import com.procurement.revision.infrastructure.web.dto.ApiResponse
import com.procurement.revision.infrastructure.web.dto.ApiSuccessResponse
import com.procurement.revision.infrastructure.web.dto.getFullErrorCode
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
                    log.debug("${action.value} has been executed. Result: ${result.get.toJson()}")
                return ApiSuccessResponse(version = version, id = id, result = result.get)
            }
            is Result.Failure ->
                ApiFailResponse(
                    version = version,
                    id = id,
                    result = listOf(
                        ApiFailResponse.Error(
                            code = getFullErrorCode(result.error.code),
                            description = result.error.description
                        )
                    )
                )
        }
    }

    abstract fun execute(node: JsonNode): Result<R, ValidationError>
}