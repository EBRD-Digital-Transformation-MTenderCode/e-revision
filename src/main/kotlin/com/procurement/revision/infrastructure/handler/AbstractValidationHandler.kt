package com.procurement.revision.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.revision.application.service.Logger
import com.procurement.revision.domain.functional.ValidationResult
import com.procurement.revision.infrastructure.fail.Fail
import com.procurement.revision.infrastructure.web.dto.Action
import com.procurement.revision.infrastructure.web.dto.ApiResponse
import com.procurement.revision.infrastructure.web.dto.ApiSuccessResponse
import com.procurement.revision.infrastructure.web.dto.generateResponseOnFailure
import com.procurement.revision.infrastructure.web.dto.tryGetId
import com.procurement.revision.infrastructure.web.dto.tryGetVersion

abstract class AbstractValidationHandler<ACTION : Action, E : Fail>(private val logger: Logger) : Handler<ACTION, ApiResponse> {

    override fun handle(node: JsonNode): ApiResponse {
        val id = node.tryGetId().get
        val version = node.tryGetVersion().get

        return when (val result = execute(node)) {
            is ValidationResult.Ok -> {
                if (logger.isDebugEnabled)
                    logger.debug("${action.key} has been executed.")
                ApiSuccessResponse(version = version, id = id)
            }
            is ValidationResult.Fail -> generateResponseOnFailure(
                fail = result.error,
                version = version,
                id = id,
                logger = logger
            )
        }
    }

    abstract fun execute(node: JsonNode): ValidationResult<E>
}
