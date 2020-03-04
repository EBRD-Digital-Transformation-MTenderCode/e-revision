package com.procurement.revision.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.revision.application.service.AmendmentService
import com.procurement.revision.domain.functional.ValidationResult
import com.procurement.revision.infrastructure.converter.convert
import com.procurement.revision.infrastructure.fail.Fail
import com.procurement.revision.infrastructure.web.dto.CommandType
import com.procurement.revision.infrastructure.web.dto.request.amendment.DataValidationRequest
import com.procurement.revision.infrastructure.web.dto.tryGetParams
import org.springframework.stereotype.Component

@Component
class DataValidationHandler(private val amendmentService: AmendmentService) : AbstractValidationHandler<CommandType, Fail>() {

    override val action: CommandType = CommandType.DATA_VALIDATION

    override fun execute(node: JsonNode): ValidationResult<List<Fail>> {
        val params = node
            .tryGetParams(DataValidationRequest::class.java)
            .doOnError { error -> return ValidationResult.error(listOf(error)) }
            .get
            .convert()
            .doOnError { errors -> return ValidationResult.error(errors) }
            .get

        return amendmentService.validateDocumentsTypes(params).map { fail -> listOf(fail) }
    }
}