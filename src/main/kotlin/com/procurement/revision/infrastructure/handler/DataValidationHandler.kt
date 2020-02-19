package com.procurement.revision.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.revision.application.service.AmendmentService
import com.procurement.revision.infrastructure.converter.convert
import com.procurement.revision.infrastructure.utils.toObject
import com.procurement.revision.infrastructure.web.dto.ApiResponse2
import com.procurement.revision.infrastructure.web.dto.ApiSuccessResponse2
import com.procurement.revision.infrastructure.web.dto.getBy
import com.procurement.revision.infrastructure.web.dto.getId
import com.procurement.revision.infrastructure.web.dto.getVersion
import com.procurement.revision.infrastructure.web.dto.request.amendment.DataValidationRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class DataValidationHandler(private val amendmentService: AmendmentService) {

    companion object {
        private val log = LoggerFactory.getLogger(DataValidationHandler::class.java)
    }

    fun handle(node: JsonNode): ApiResponse2 {
        val request = node.getBy("params").toObject(DataValidationRequest::class.java)
        val params = request.convert()
        amendmentService.validateDocumentsTypes(params)

        if (log.isDebugEnabled)
            log.debug("Amendments have been validated.")

        return ApiSuccessResponse2(
            version = node.getVersion(),
            id = node.getId(),
            result = null
        )
    }
}