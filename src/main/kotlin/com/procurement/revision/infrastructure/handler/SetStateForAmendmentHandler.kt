package com.procurement.revision.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.revision.application.model.amendment.state.SetStateForAmendmentResult
import com.procurement.revision.application.repository.HistoryRepository
import com.procurement.revision.application.service.AmendmentService
import com.procurement.revision.application.service.Logger
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.infrastructure.converter.convert
import com.procurement.revision.infrastructure.fail.Fail
import com.procurement.revision.infrastructure.web.dto.CommandType
import com.procurement.revision.infrastructure.web.dto.request.amendment.SetStateForAmendmentRequest
import com.procurement.revision.infrastructure.web.dto.tryGetParams
import org.springframework.stereotype.Component

@Component
class SetStateForAmendmentHandler(
    private val amendmentService: AmendmentService,
    historyRepository: HistoryRepository,
    logger: Logger
) : AbstractHistoricalHandler<CommandType, SetStateForAmendmentResult>(
    SetStateForAmendmentResult::class.java,
    historyRepository,
    logger
) {
    override val action: CommandType = CommandType.SET_STATE_FOR_AMENDMENT

    override fun execute(node: JsonNode): Result<SetStateForAmendmentResult, Fail> {
        val params = node
            .tryGetParams(SetStateForAmendmentRequest::class.java)
            .forwardResult { error -> return error }
            .convert()
            .forwardResult { errors -> return errors }

        return amendmentService.setStateForAmendment(params)
    }
}