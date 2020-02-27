package com.procurement.revision.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.revision.application.model.amendment.CreateAmendmentResult
import com.procurement.revision.application.service.AmendmentService
import com.procurement.revision.domain.util.Result
import com.procurement.revision.infrastructure.converter.convert
import com.procurement.revision.infrastructure.fail.Fail
import com.procurement.revision.infrastructure.fail.error.RequestError
import com.procurement.revision.infrastructure.repository.HistoryRepository
import com.procurement.revision.infrastructure.web.dto.CommandType
import com.procurement.revision.infrastructure.web.dto.request.amendment.CreateAmendmentRequest
import com.procurement.revision.infrastructure.web.dto.tryGetParams
import org.springframework.stereotype.Component

@Component
class CreateAmendmentHandler(
    private val amendmentService: AmendmentService,
    historyRepository: HistoryRepository
) : AbstractHistoricalHandler<CommandType, CreateAmendmentResult>(
    CreateAmendmentResult::class.java,
    historyRepository
) {
    override val action: CommandType = CommandType.CREATE_AMENDMENT

    override fun execute(node: JsonNode): Result<CreateAmendmentResult, Fail> {
        val request = when (val result = node.tryGetParams(CreateAmendmentRequest::class.java)) {
            is Result.Success -> result.get
            is Result.Failure -> {
                return Result.failure(RequestError.ParsingError(result.error.description))
            }
        }
        val params = request.convert()
        return amendmentService.createAmendment(params)
    }
}