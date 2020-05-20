package com.procurement.revision.infrastructure.converter

import com.procurement.revision.application.model.amendment.part.GetAmendmentByIdsParams
import com.procurement.revision.application.model.amendment.part.GetAmendmentByIdsResult
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.model.amendment.Amendment
import com.procurement.revision.infrastructure.fail.error.DataErrors
import com.procurement.revision.infrastructure.web.dto.request.amendment.GetAmendmentByIdsRequest

fun GetAmendmentByIdsRequest.convert(): Result<GetAmendmentByIdsParams, DataErrors> =
    GetAmendmentByIdsParams.tryCreate(cpid = cpid, ocid = ocid, amendmentIds = amendmentIds)

fun Amendment.convertToGetAmendmentByIdsResult() =
    GetAmendmentByIdsResult(
        status = status,
        type = type,
        relatesTo = relatesTo,
        documents = documents.map { document ->
            GetAmendmentByIdsResult.Document(
                id = document.id,
                description = document.description,
                title = document.title,
                documentType = document.documentType
            )
        },
        id = id,
        relatedItem = relatedItem,
        rationale = rationale,
        description = description,
        date = date
    )