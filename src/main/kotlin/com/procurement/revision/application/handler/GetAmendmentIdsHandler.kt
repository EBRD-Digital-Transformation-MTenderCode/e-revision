package com.procurement.revision.application.handler

import com.procurement.revision.application.model.amendment.GetAmendmentIdsData
import com.procurement.revision.application.model.amendment.GetAmendmentIdsResult
import com.procurement.revision.application.repository.AmendmentRepository
import com.procurement.revision.domain.model.amendment.Amendment
import com.procurement.revision.domain.model.amendment.AmendmentId
import org.springframework.stereotype.Component
import java.util.function.Predicate

@Component
class GetAmendmentIdsHandler(private val amendmentRepository: AmendmentRepository) {

    fun handle(data: GetAmendmentIdsData): List<AmendmentId> {
        val amendments = amendmentRepository.findBy(data.cpid)
        val relatedItems = data.relatedItems.toSet()

        return amendments
            .asSequence()
            .filter { amendment ->
                testEquals(amendment.status, pattern = data.status)
                    && testEquals(amendment.type, pattern = data.type)
                    && testEquals(amendment.relatesTo, pattern = data.relatesTo)
                    && testContains(amendment.relatedItem, patterns = relatedItems)
            }
            .map { amendment -> amendment.id }
            .toList()
    }

    private fun <T> testEquals(value: T, pattern: T?): Boolean = if (pattern != null) value == pattern else true
    private fun <T> testContains(value: T, patterns: Set<T>): Boolean =
        if (patterns.isNotEmpty()) value in patterns else true
}