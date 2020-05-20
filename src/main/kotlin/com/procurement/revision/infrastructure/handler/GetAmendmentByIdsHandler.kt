package com.procurement.revision.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.revision.application.model.amendment.part.GetAmendmentByIdsResult
import com.procurement.revision.application.service.AmendmentService
import com.procurement.revision.application.service.Logger
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.infrastructure.converter.convert
import com.procurement.revision.infrastructure.fail.Fail
import com.procurement.revision.infrastructure.web.dto.CommandType
import com.procurement.revision.infrastructure.web.dto.request.amendment.GetAmendmentByIdsRequest
import com.procurement.revision.infrastructure.web.dto.tryGetParams
import org.springframework.stereotype.Component

@Component
class GetAmendmentByIdsHandler(
    private val amendmentService: AmendmentService, logger: Logger
) : AbstractHandler<CommandType, List<GetAmendmentByIdsResult>>(logger) {

    override val action: CommandType = CommandType.GET_AMENDMENT_BY_IDS

    override fun execute(node: JsonNode): Result<List<GetAmendmentByIdsResult>, Fail> {
        val params = node
            .tryGetParams(GetAmendmentByIdsRequest::class.java)
            .orForwardFail { error -> return error }
            .convert()
            .orForwardFail { error -> return error }

        return amendmentService.getAmendmentByIds(params = params)
    }
}