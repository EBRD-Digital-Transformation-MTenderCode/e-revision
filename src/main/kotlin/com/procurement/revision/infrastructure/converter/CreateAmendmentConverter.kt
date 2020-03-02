package com.procurement.revision.infrastructure.converter

import com.procurement.revision.application.model.amendment.CreateAmendmentParams
import com.procurement.revision.application.model.amendment.CreateAmendmentResult
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.model.amendment.Amendment
import com.procurement.revision.domain.util.extension.mapOptionalResult
import com.procurement.revision.infrastructure.fail.Fail
import com.procurement.revision.infrastructure.web.dto.request.amendment.CreateAmendmentRequest

fun CreateAmendmentRequest.convert(): Result<CreateAmendmentParams, Fail> {
    val amendment = this.amendment.convert()
    if (amendment.isFail)
        return Result.failure(amendment.error)

    return CreateAmendmentParams.tryCreate(
        amendment = amendment.get,
        ocid = ocid,
        cpid = cpid,
        operationType = operationType,
        startDate = startDate,
        owner = owner,
        id = id
    )
}

private fun CreateAmendmentRequest.Amendment.convert(): Result<CreateAmendmentParams.Amendment, Fail> {
    val documents = this.documents.mapOptionalResult { it.convert() }
    if (documents.isFail)
        return Result.failure(documents.error)

    return CreateAmendmentParams.Amendment.tryCreate(
        rationale = this.rationale,
        description = this.description,
        documents = documents.get,
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
    amendment = CreateAmendmentResult.Amendment(
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
            CreateAmendmentResult.Amendment.Document(
                id = document.id,
                description = document.description,
                documentType = document.documentType,
                title = document.title
            )
        }
    )
)