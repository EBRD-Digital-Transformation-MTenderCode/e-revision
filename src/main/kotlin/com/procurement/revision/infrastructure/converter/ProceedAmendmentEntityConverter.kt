package com.procurement.revision.infrastructure.dto.converter

import com.procurement.revision.application.model.amendment.ProceedAmendmentResult
import com.procurement.revision.domain.model.Amendment

fun Amendment.convert(): ProceedAmendmentResult {
    return ProceedAmendmentResult(
        amendment = this
            .let { amendment ->
                ProceedAmendmentResult.Amendment(
                    rationale = amendment.rationale,
                    description = amendment.description,
                    documents = amendment.documents
                        .map { document ->
                            ProceedAmendmentResult.Amendment.Document(
                                id = document.id,
                                documentType = document.documentType,
                                title = document.title,
                                description = document.description
                            )
                        },
                    date = amendment.date,
                    status = amendment.status,
                    id = amendment.id,
                    relatedItem = amendment.relatedItem,
                    relatesTo = amendment.relatesTo,
                    type = amendment.type
                )
            }
    )
}
