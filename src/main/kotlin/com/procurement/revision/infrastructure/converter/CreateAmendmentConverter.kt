package com.procurement.revision.infrastructure.converter

import com.procurement.revision.application.model.amendment.CreateAmendmentParams
import com.procurement.revision.application.model.amendment.CreateAmendmentResult
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.functional.Result.Companion.failure
import com.procurement.revision.domain.model.amendment.Amendment
import com.procurement.revision.domain.util.extension.mapOptionalResult
import com.procurement.revision.infrastructure.fail.error.DataErrors
import com.procurement.revision.infrastructure.web.dto.request.amendment.CreateAmendmentRequest

fun CreateAmendmentRequest.convert(): Result<CreateAmendmentParams, DataErrors> {
    val amendment = this.amendment
        .convert()
        .orForwardFail { error -> return error }

    return CreateAmendmentParams.tryCreate(
        amendment = amendment,
        ocid = ocid,
        cpid = cpid,
        operationType = operationType,
        date = date,
        owner = owner,
        relatedEntityId = relatedEntityId
    )
}

private fun CreateAmendmentRequest.Amendment.convert(): Result<CreateAmendmentParams.Amendment, DataErrors> {
    val documents = this.documents
        .mapOptionalResult { it.convert() }
        .orForwardFail { error -> return error }

    return CreateAmendmentParams.Amendment.tryCreate(
        rationale = this.rationale,
        description = this.description,
        documents = documents,
        id = this.id
    )
}

fun CreateAmendmentRequest.Amendment.Document.convert() =
    CreateAmendmentParams.Amendment.Document.tryCreate(
        documentType = this.documentType,
        id = this.id,
        title = this.title,
        description = this.description
    )

fun Amendment.convertToCreateAmendmentResult() = CreateAmendmentResult(
    rationale = rationale,
    description = description,
    relatedItem = relatedItem,
    relatesTo = relatesTo,
    status = status,
    id = id,
    type = type,
    date = date,
    token = token,
    documents = documents.map { document ->
        CreateAmendmentResult.Document(
            id = document.id,
            description = document.description,
            documentType = document.documentType,
            title = document.title
        )
    }

)