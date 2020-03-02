package com.procurement.revision.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.revision.application.service.AmendmentService
import com.procurement.revision.domain.functional.Result
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

    override fun execute(node: JsonNode): ValidationResult<Fail> {
        val request = when (val result = node.tryGetParams(DataValidationRequest::class.java)) {
            is Result.Success -> result.get
            is Result.Failure -> {
                return ValidationResult.error(result.error)
            }
        }

        val params = request.convert()
        if (params.isFail) return ValidationResult.error(params.error)
        return amendmentService.validateDocumentsTypes(params.get)
    }
}