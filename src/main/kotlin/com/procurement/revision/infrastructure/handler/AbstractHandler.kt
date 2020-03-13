package com.procurement.revision.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.revision.application.service.Logger
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.infrastructure.fail.Fail
import com.procurement.revision.infrastructure.utils.toJson
import com.procurement.revision.infrastructure.web.dto.Action
import com.procurement.revision.infrastructure.web.dto.ApiResponse
import com.procurement.revision.infrastructure.web.dto.ApiSuccessResponse
import com.procurement.revision.infrastructure.web.dto.generateResponseOnFailure
import com.procurement.revision.infrastructure.web.dto.tryGetId
import com.procurement.revision.infrastructure.web.dto.tryGetVersion

abstract class AbstractHandler<ACTION : Action, R : Any>(private val logger: Logger) : Handler<ACTION, ApiResponse> {

    override fun handle(node: JsonNode): ApiResponse {
        val id = node.tryGetId().get
        val version = node.tryGetVersion().get

        return when (val result = execute(node)) {
            is Result.Success -> {
                if (logger.isDebugEnabled)
                    logger.debug("${action.key} has been executed. Result: ${result.get.toJson()}")
                return ApiSuccessResponse(version = version, id = id, result = result.get)
            }
            is Result.Failure -> generateResponseOnFailure(
                fail = result.error,
                version = version,
                id = id,
                logger = logger
            )
        }
    }

    abstract fun execute(node: JsonNode): Result<R, Fail>
}