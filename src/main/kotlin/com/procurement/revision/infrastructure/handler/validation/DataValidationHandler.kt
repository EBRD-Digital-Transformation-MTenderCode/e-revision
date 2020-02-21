package com.procurement.revision.infrastructure.handler.validation

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.revision.application.service.AmendmentService
import com.procurement.revision.domain.ValidationResult
import com.procurement.revision.infrastructure.converter.convert
import com.procurement.revision.infrastructure.handler.AbstractValidationHandler
import com.procurement.revision.infrastructure.web.dto.Command2Type
import com.procurement.revision.infrastructure.web.dto.getParams
import com.procurement.revision.infrastructure.web.dto.request.amendment.DataValidationRequest
import org.springframework.stereotype.Component

@Component
class DataValidationHandler(private val amendmentService: AmendmentService) : AbstractValidationHandler<Command2Type, ValidationError>() {

    override val action: Command2Type = Command2Type.DATA_VALIDATION

    override fun execute(node: JsonNode): ValidationResult<ValidationError> {
        val request = node.getParams(DataValidationRequest::class.java)
        val params = request.convert()
        return amendmentService.validateDocumentsTypes(params)
    }
}