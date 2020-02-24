package com.procurement.revision.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.revision.domain.ValidationResult
import com.procurement.revision.infrastructure.handler.validation.ValidationError
import com.procurement.revision.infrastructure.utils.toJson
import com.procurement.revision.infrastructure.web.dto.Action
import com.procurement.revision.infrastructure.web.dto.ApiFailResponse
import com.procurement.revision.infrastructure.web.dto.ApiResponse
import com.procurement.revision.infrastructure.web.dto.ApiSuccessResponse
import com.procurement.revision.infrastructure.web.dto.getFullErrorCode
import com.procurement.revision.infrastructure.web.dto.getId
import com.procurement.revision.infrastructure.web.dto.getVersion
import org.slf4j.LoggerFactory

abstract class AbstractValidationHandler<ACTION : Action, E : ValidationError> : Handler<ACTION, ApiResponse> {
    companion object {
        private val log = LoggerFactory.getLogger(AbstractValidationHandler::class.java)
    }

    override fun handle(node: JsonNode): ApiResponse {
        val id = node.getId()
        val version = node.getVersion()

        val result = execute(node)

        if (log.isDebugEnabled)
            log.debug("${action.value} has been executed. Result: ${result.toJson()}")
        return if (result.isOk)
            ApiSuccessResponse(version = version, id = id, result = null)
        else
            ApiFailResponse(
                version = version,
                id = id,
                result = listOf(
                    ApiFailResponse.Error(
                        code = getFullErrorCode(result.get.code),
                        description = result.get.description
                    )
                )
            )
    }

    abstract fun execute(node: JsonNode): ValidationResult<E>
}
