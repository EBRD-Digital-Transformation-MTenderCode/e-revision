package com.procurement.revision.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.revision.application.model.amendment.CreateAmendmentResult
import com.procurement.revision.application.repository.HistoryRepository
import com.procurement.revision.application.service.AmendmentService
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.infrastructure.converter.convert
import com.procurement.revision.infrastructure.fail.Fail
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

    override fun execute(node: JsonNode): Result<CreateAmendmentResult, List<Fail>> {
        val params = node
            .tryGetParams(CreateAmendmentRequest::class.java)
            .doOnError { error -> return Result.failure(listOf(error)) }
            .get
            .convert()
            .doOnError { errors -> return Result.failure(errors) }
            .get


        return amendmentService.createAmendment(params).mapError { fail -> listOf(fail) }
    }
}