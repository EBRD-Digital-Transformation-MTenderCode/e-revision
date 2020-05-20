package com.procurement.revision.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.revision.application.service.AmendmentService
import com.procurement.revision.application.service.Logger
import com.procurement.revision.domain.functional.ValidationResult
import com.procurement.revision.infrastructure.converter.convert
import com.procurement.revision.infrastructure.fail.Fail
import com.procurement.revision.infrastructure.web.dto.CommandType
import com.procurement.revision.infrastructure.web.dto.request.amendment.CheckAccessToAmendmentRequest
import com.procurement.revision.infrastructure.web.dto.tryGetParams
import org.springframework.stereotype.Component

@Component
class CheckAccessToAmendmentHandler(
    private val amendmentService: AmendmentService, logger: Logger
) : AbstractValidationHandler<CommandType, Fail>(logger) {

    override val action: CommandType = CommandType.CHECK_ACCESS_TO_AMENDMENT

    override fun execute(node: JsonNode): ValidationResult<Fail> {
        val params = node
            .tryGetParams(CheckAccessToAmendmentRequest::class.java)
            .doOnError { error -> return ValidationResult.error(error) }
            .get
            .convert()
            .doOnError { errors -> return ValidationResult.error(errors) }
            .get

        return amendmentService.checkAccessToAmendment(params)
    }
}