package com.procurement.revision.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.revision.application.service.AmendmentService
import com.procurement.revision.application.service.Logger
import com.procurement.revision.domain.errorIfBlank
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.functional.Result.Companion.failure
import com.procurement.revision.domain.functional.ValidationResult
import com.procurement.revision.domain.functional.asSuccess
import com.procurement.revision.infrastructure.converter.convert
import com.procurement.revision.infrastructure.fail.Fail
import com.procurement.revision.infrastructure.fail.error.DataErrors
import com.procurement.revision.infrastructure.handler.exception.EmptyStringException
import com.procurement.revision.infrastructure.web.dto.CommandType
import com.procurement.revision.infrastructure.web.dto.request.amendment.DataValidationRequest
import com.procurement.revision.infrastructure.web.dto.tryGetParams
import org.springframework.stereotype.Component

@Component
class DataValidationHandler(
    private val amendmentService: AmendmentService, logger: Logger
) : AbstractValidationHandler<CommandType, Fail>(logger) {

    override val action: CommandType = CommandType.DATA_VALIDATION

    override fun execute(node: JsonNode): ValidationResult<Fail> {
        val params = node
            .tryGetParams(DataValidationRequest::class.java)
            .doReturn { return ValidationResult.error(it) }
            .validateTextAttributes()
            .doReturn { return ValidationResult.error(it) }
            .convert()
            .doReturn { errors -> return ValidationResult.error(errors) }

        return amendmentService.validateDocumentsTypes(params)
    }

    private fun DataValidationRequest.validateTextAttributes(): Result<DataValidationRequest, DataErrors.Validation.EmptyString> {
        try {
            amendment.rationale.checkForBlank("amendment.rationale")
            amendment.description.checkForBlank("amendment.description")
            amendment.documents?.forEachIndexed { i, document ->
                document.title.checkForBlank("amendment.documents[$i].title")
                document.description.checkForBlank("amendment.documents[$i].description")
            }
        } catch (exception: EmptyStringException) {
            return failure(DataErrors.Validation.EmptyString(exception.attributeName))
        }
        return this.asSuccess()
    }

    private fun String?.checkForBlank(name: String) = this.errorIfBlank { EmptyStringException(name) }
}