package com.procurement.revision.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.revision.application.repository.HistoryRepository
import com.procurement.revision.application.service.Logger
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.infrastructure.fail.Fail
import com.procurement.revision.infrastructure.utils.toJson
import com.procurement.revision.infrastructure.utils.tryToObject
import com.procurement.revision.infrastructure.web.dto.Action
import com.procurement.revision.infrastructure.web.dto.ApiResponse
import com.procurement.revision.infrastructure.web.dto.ApiSuccessResponse
import com.procurement.revision.infrastructure.web.dto.generateResponseOnFailure
import com.procurement.revision.infrastructure.web.dto.tryGetId
import com.procurement.revision.infrastructure.web.dto.tryGetVersion

abstract class AbstractHistoricalHandler<ACTION : Action, R : Any>(
    private val target: Class<R>,
    private val historyRepository: HistoryRepository,
    private val logger: Logger
) : Handler<ACTION, ApiResponse> {

    override fun handle(node: JsonNode): ApiResponse {
        val id = node.tryGetId().get
        val version = node.tryGetVersion().get

        val history = historyRepository.getHistory(id.toString(), action.key)
            .doOnError { error ->
                return generateResponseOnFailure(
                    fail = error,
                    version = version,
                    id = id,
                    logger = logger
                )
            }
            .get
        if (history != null) {
            val data = history.jsonData
            val result = data.tryToObject(target)
                .doOnError {
                    return generateResponseOnFailure(
                        fail = Fail.Incident.ParseFromDatabaseIncident(data),
                        id = id,
                        version = version,
                        logger = logger
                    )
                }.get
            return ApiSuccessResponse(version = version, id = id, result = result)
        }

        return when (val result = execute(node)) {
            is Result.Success -> {
                val resultData = result.get
                historyRepository.saveHistory(id.toString(), action.key, resultData)
                if (logger.isDebugEnabled)
                    logger.debug("${action.key} has been executed. Result: ${resultData.toJson()}")

                ApiSuccessResponse(version = version, id = id, result = resultData)
            }
            is Result.Failure -> generateResponseOnFailure(fail = result.error, version = version, id = id, logger = logger)
        }
    }

    abstract fun execute(node: JsonNode): Result<R, Fail>
}

