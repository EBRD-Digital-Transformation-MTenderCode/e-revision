package com.procurement.revision.application.handler

import com.procurement.revision.application.model.amendment.GetAmendmentIdsData
import com.procurement.revision.application.model.amendment.GetAmendmentIdsResult
import com.procurement.revision.application.repository.AmendmentRepository
import com.procurement.revision.domain.model.amendment.Amendment
import com.procurement.revision.domain.model.amendment.AmendmentFilter
import org.springframework.stereotype.Component
import java.util.function.Predicate

@Component
class GetAmendmentIdsHandler(private val amendmentRepository: AmendmentRepository) {

    fun handle(data: GetAmendmentIdsData): List<GetAmendmentIdsResult> {
        val amendments = amendmentRepository.findBy(data.cpid)

        val isAmendmentSuitable = data.relatedItems
            ?.asSequence()
            ?.map { item ->
                Predicate<Amendment> { amendment ->
                    AmendmentFilter(
                        status = data.status,
                        relatesTo = data.relatesTo,
                        type = data.type,
                        relatedItem = item
                    ).compareWith(amendment)
                }
            }?.reduce { accumulatedPredicate, predicate -> accumulatedPredicate.or(predicate) }

        return amendments
            .asSequence()
            .filter { amendment -> isAmendmentSuitable?.test(amendment) ?: true }
            .map { amendment -> GetAmendmentIdsResult(amendment.id) }
            .toList()
    }
}
