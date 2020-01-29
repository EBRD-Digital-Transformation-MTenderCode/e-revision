package com.procurement.revision.infrastructure.dto.converter

import com.procurement.revision.application.service.amendment.ProceedAmendmentResult
import com.procurement.revision.infrastructure.dto.amendment.ProceedAmendmentResponse

fun ProceedAmendmentResult.convert(): ProceedAmendmentResponse {
    return ProceedAmendmentResponse(
        amendment = this.amendment
            .let { amendment ->
                ProceedAmendmentResponse.Amendment(
                    rationale = amendment.rationale,
                    description = amendment.description,
                    documents = amendment.documents
                        .map { document ->
                            ProceedAmendmentResponse.Amendment.Document(
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
