package com.procurement.revision.infrastructure.converter

import com.procurement.revision.application.model.amendment.part.GetMainPartOfAmendmentParams
import com.procurement.revision.application.model.amendment.part.GetMainPartOfAmendmentResult
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.functional.Result.Companion.failure
import com.procurement.revision.domain.model.amendment.Amendment
import com.procurement.revision.infrastructure.fail.error.DataErrors
import com.procurement.revision.infrastructure.web.dto.request.amendment.GetMainPartOfAmendmentRequest

fun GetMainPartOfAmendmentRequest.convert(): Result<GetMainPartOfAmendmentParams, DataErrors> =
    GetMainPartOfAmendmentParams.tryCreate(
        cpid = cpid, ocid = ocid, amendmentIds = amendmentIds
    ).doOnError { error -> return failure(error) }

fun Amendment.convertToGetMainPartOfAmendmentResult() =
    GetMainPartOfAmendmentResult(
        id = id,
        status = status,
        relateditem = relatedItem,
        relatesTo = relatesTo,
        type = type
    )