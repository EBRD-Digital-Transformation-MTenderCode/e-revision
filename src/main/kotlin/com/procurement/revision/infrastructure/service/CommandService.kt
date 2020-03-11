package com.procurement.revision.infrastructure.service

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.revision.infrastructure.handler.CreateAmendmentHandler
import com.procurement.revision.infrastructure.handler.DataValidationHandler
import com.procurement.revision.infrastructure.handler.GetAmendmentIdsHandler
import com.procurement.revision.infrastructure.web.dto.ApiResponse
import com.procurement.revision.infrastructure.web.dto.CommandType
import com.procurement.revision.infrastructure.web.dto.tryGetAction
import org.springframework.stereotype.Service

@Service
class CommandService(
    private val getAmendmentIdsHandler: GetAmendmentIdsHandler,
    private val dataValidationHandler: DataValidationHandler,
    private val createAmendmentHandler: CreateAmendmentHandler
) {

    fun execute(node: JsonNode): ApiResponse {
        val action = node.tryGetAction().get

        return when (action) {
            CommandType.GET_AMENDMENTS_IDS -> {
                getAmendmentIdsHandler.handle(node)
            }
            CommandType.DATA_VALIDATION -> {
                dataValidationHandler.handle(node)
            }
            CommandType.CREATE_AMENDMENT -> {
                createAmendmentHandler.handle(node)
            }
        }
    }
}