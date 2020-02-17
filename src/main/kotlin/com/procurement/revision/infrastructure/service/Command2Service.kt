package com.procurement.revision.infrastructure.service

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.revision.application.handler.GetAmendmentIdsHandler
import com.procurement.revision.infrastructure.web.dto.ApiResponse2
import com.procurement.revision.infrastructure.web.dto.Command2Type
import com.procurement.revision.infrastructure.web.dto.getAction
import org.springframework.stereotype.Service

@Service
class Command2Service(
    private val getAmendmentIdsHandler: GetAmendmentIdsHandler
) {

    fun execute(node: JsonNode): ApiResponse2 {
        val action = node.getAction()
        return when (action) {
            Command2Type.GET_AMENDMENTS_IDS -> {
                getAmendmentIdsHandler.handle(node)
            }
        }
    }
}