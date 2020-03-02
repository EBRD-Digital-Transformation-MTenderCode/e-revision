package com.procurement.revision.infrastructure.converter

import com.procurement.revision.application.model.amendment.GetAmendmentIdsParams
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.infrastructure.fail.Fail
import com.procurement.revision.infrastructure.web.dto.request.amendment.GetAmendmentIdsRequest

fun GetAmendmentIdsRequest.convert(): Result<GetAmendmentIdsParams, Fail>  =
    GetAmendmentIdsParams.tryCreate(
        status = status,
        relatedItems = relatedItems,
        cpid = cpid,
        relatesTo = relatesTo,
        type = type,
        ocid = ocid
    )


