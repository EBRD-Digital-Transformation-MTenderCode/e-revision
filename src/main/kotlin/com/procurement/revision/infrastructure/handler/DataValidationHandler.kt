package com.procurement.revision.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.revision.application.service.AmendmentService
import com.procurement.revision.infrastructure.converter.convert
import com.procurement.revision.infrastructure.utils.toObject
import com.procurement.revision.infrastructure.web.dto.Command2Type
import com.procurement.revision.infrastructure.web.dto.getBy
import com.procurement.revision.infrastructure.web.dto.request.amendment.DataValidationRequest
import org.springframework.stereotype.Component

@Component
class DataValidationHandler(private val amendmentService: AmendmentService) : AbstractHandler<Command2Type, Unit>() {

    override val action: Command2Type = Command2Type.DATA_VALIDATION

    override fun execute(node: JsonNode): Unit? {
        val request = node.getBy("params").toObject(DataValidationRequest::class.java)
        val params = request.convert()
        amendmentService.validateDocumentsTypes(params)
        return null
    }
}