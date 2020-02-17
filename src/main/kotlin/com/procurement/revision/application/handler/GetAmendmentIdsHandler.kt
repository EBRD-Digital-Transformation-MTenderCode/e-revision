package com.procurement.revision.application.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.revision.application.repository.AmendmentRepository
import com.procurement.revision.infrastructure.converter.convert
import com.procurement.revision.infrastructure.utils.toJson
import com.procurement.revision.infrastructure.utils.toObject
import com.procurement.revision.infrastructure.web.dto.ApiResponse2
import com.procurement.revision.infrastructure.web.dto.ApiSuccessResponse2
import com.procurement.revision.infrastructure.web.dto.getId
import com.procurement.revision.infrastructure.web.dto.getBy
import com.procurement.revision.infrastructure.web.dto.getVersion
import com.procurement.revision.infrastructure.web.dto.request.amendment.GetAmendmentIdsRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class GetAmendmentIdsHandler(private val amendmentRepository: AmendmentRepository) {

    companion object {
        private val log = LoggerFactory.getLogger(GetAmendmentIdsHandler::class.java)
    }

    fun handle(node: JsonNode): ApiResponse2 {
        val request = node.getBy("params").toObject(GetAmendmentIdsRequest::class.java)
        val params = request.convert()
        val amendments = amendmentRepository.findBy(params.cpid)
        val relatedItems = params.relatedItems.toSet()

        val result = amendments
            .asSequence()
            .filter { amendment ->
                testEquals(amendment.status, pattern = params.status)
                    && testEquals(amendment.type, pattern = params.type)
                    && testEquals(amendment.relatesTo, pattern = params.relatesTo)
                    && testContains(amendment.relatedItem, patterns = relatedItems)
            }
            .map { amendment -> amendment.id }
            .toList()

        if (log.isDebugEnabled)
            log.debug("Amendment ids have been found. Result: ${result.toJson()}")

        val id = node.getId()
        val version = node.getVersion()

        return ApiSuccessResponse2(
            version = version,
            id = id,
            result = result
        )
    }

    private fun <T> testEquals(value: T, pattern: T?): Boolean = if (pattern != null) value == pattern else true
    private fun <T> testContains(value: T, patterns: Set<T>): Boolean =
        if (patterns.isNotEmpty()) value in patterns else true
}