package com.procurement.revision.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.revision.application.model.amendment.CreateAmendmentResult
import com.procurement.revision.application.service.AmendmentService
import com.procurement.revision.infrastructure.converter.convert
import com.procurement.revision.infrastructure.repository.HistoryRepository
import com.procurement.revision.infrastructure.utils.toJson
import com.procurement.revision.infrastructure.utils.toObject
import com.procurement.revision.infrastructure.web.dto.ApiResponse2
import com.procurement.revision.infrastructure.web.dto.ApiSuccessResponse2
import com.procurement.revision.infrastructure.web.dto.getAction
import com.procurement.revision.infrastructure.web.dto.getBy
import com.procurement.revision.infrastructure.web.dto.getId
import com.procurement.revision.infrastructure.web.dto.getVersion
import com.procurement.revision.infrastructure.web.dto.request.amendment.CreateAmendmentRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CreateAmendmentHandler(
    private val amendmentService: AmendmentService,
    private val historyRepository: HistoryRepository
) {

    companion object {
        private val log = LoggerFactory.getLogger(CreateAmendmentHandler::class.java)
    }

    fun handle(node: JsonNode): ApiResponse2 {
        val id = node.getId()
        val action = node.getAction().toString()
        val version = node.getVersion()

        val history = historyRepository.getHistory(id.toString(), action)
        if (history != null) {
            val result = history.jsonData.toObject(CreateAmendmentResult::class.java)
            return ApiSuccessResponse2(version = version, id = id, result = result)
        }

        val request = node.getBy("params").toObject(CreateAmendmentRequest::class.java)
        val params = request.convert()

        val result = amendmentService.createAmendment(params)

        historyRepository.saveHistory(id.toString(), action, result)
        if (log.isDebugEnabled)
            log.debug("Amendment has been created. Response: ${result.toJson()}")

        return ApiSuccessResponse2(version = version, id = id, result = result)
    }
}