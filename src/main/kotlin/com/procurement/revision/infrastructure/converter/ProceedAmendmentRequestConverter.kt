package com.procurement.revision.infrastructure.dto.converter

import com.procurement.revision.application.model.amendment.ProceedAmendmentData
import com.procurement.revision.application.exception.ErrorException
import com.procurement.revision.application.exception.ErrorType
import com.procurement.revision.infrastructure.dto.amendment.ProceedAmendmentRequest
import com.procurement.revision.lib.errorIfEmpty

fun ProceedAmendmentRequest.convert(): ProceedAmendmentData {
    return ProceedAmendmentData(
        amendment = this.amendment
            .let { amendment ->
                ProceedAmendmentData.Amendment(
                    rationale = amendment.rationale,
                    description = amendment.description,
                    documents = amendment.documents
                        .errorIfEmpty {
                            ErrorException(
                                error = ErrorType.COLLECTION_IS_EMPTY,
                                message = "The amendments contain empty list of the documents."
                            )
                        }
                        ?.map { document ->
                            ProceedAmendmentData.Amendment.Document(
                                id = document.id,
                                documentType = document.documentType,
                                title = document.title,
                                description = document.description
                            )
                        }
                        .orEmpty()
                )
            }
    )
}
