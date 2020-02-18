package com.procurement.revision.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.revision.application.service.AmendmentService
import com.procurement.revision.infrastructure.converter.convert
import com.procurement.revision.infrastructure.utils.toJson
import com.procurement.revision.infrastructure.utils.toObject
import com.procurement.revision.infrastructure.web.dto.ApiResponse2
import com.procurement.revision.infrastructure.web.dto.ApiSuccessResponse2
import com.procurement.revision.infrastructure.web.dto.getBy
import com.procurement.revision.infrastructure.web.dto.getId
import com.procurement.revision.infrastructure.web.dto.getVersion
import com.procurement.revision.infrastructure.web.dto.request.amendment.GetAmendmentIdsRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class GetAmendmentIdsHandler(private val amendmentService: AmendmentService) {

    companion object {
        private val log = LoggerFactory.getLogger(GetAmendmentIdsHandler::class.java)
    }

    fun handle(node: JsonNode): ApiResponse2 {
        val request = node.getBy("params").toObject(GetAmendmentIdsRequest::class.java)
        val params = request.convert()
        val result = amendmentService.getAmendmentIdsBy(params)

        if (log.isDebugEnabled)
            log.debug("Amendment ids have been found. Result: ${result.toJson()}")

        return ApiSuccessResponse2(
            version = node.getVersion(),
            id = node.getId(),
            result = result
        )
    }
}