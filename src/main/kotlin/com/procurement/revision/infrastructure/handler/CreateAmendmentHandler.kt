package com.procurement.revision.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.revision.application.model.amendment.CreateAmendmentResult
import com.procurement.revision.application.service.AmendmentService
import com.procurement.revision.infrastructure.converter.convert
import com.procurement.revision.infrastructure.repository.HistoryRepository
import com.procurement.revision.infrastructure.web.dto.CommandType
import com.procurement.revision.infrastructure.web.dto.getParams
import com.procurement.revision.infrastructure.web.dto.request.amendment.CreateAmendmentRequest
import org.springframework.stereotype.Component

@Component
class CreateAmendmentHandler(
    private val amendmentService: AmendmentService,
    historyRepository: HistoryRepository
) : AbstractHistoricalHandler<CommandType, CreateAmendmentResult>(CreateAmendmentResult::class.java, historyRepository) {
    override val action: CommandType = CommandType.CREATE_AMENDMENT

    override fun execute(node: JsonNode): CreateAmendmentResult {
        val request = node.getParams(CreateAmendmentRequest::class.java)
        val params = request.convert()
        return amendmentService.createAmendment(params)
    }
}