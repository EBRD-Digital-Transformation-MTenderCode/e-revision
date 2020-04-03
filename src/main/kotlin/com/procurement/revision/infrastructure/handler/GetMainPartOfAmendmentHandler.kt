package com.procurement.revision.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.revision.application.model.amendment.part.GetMainPartOfAmendmentResult
import com.procurement.revision.application.service.AmendmentService
import com.procurement.revision.application.service.Logger
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.infrastructure.converter.convert
import com.procurement.revision.infrastructure.fail.Fail
import com.procurement.revision.infrastructure.web.dto.CommandType
import com.procurement.revision.infrastructure.web.dto.request.amendment.GetMainPartOfAmendmentRequest
import com.procurement.revision.infrastructure.web.dto.tryGetParams
import org.springframework.stereotype.Component

@Component
class GetMainPartOfAmendmentHandler(
    private val amendmentService: AmendmentService, logger: Logger
) : AbstractHandler<CommandType, List<GetMainPartOfAmendmentResult>>(logger) {

    override val action: CommandType = CommandType.GET_MAIN_PART_OF_AMENDMENT_BY_IDS

    override fun execute(node: JsonNode): Result<List<GetMainPartOfAmendmentResult>, Fail> {
        val params = node
            .tryGetParams(GetMainPartOfAmendmentRequest::class.java)
            .forwardResult { error -> return error }
            .convert()
            .forwardResult { error -> return error }

        return amendmentService.getMainPartOfAmendment(params)
    }
}