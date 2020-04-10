package com.procurement.revision.infrastructure.converter

import com.procurement.revision.application.model.amendment.FindAmendmentIdsParams
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.infrastructure.fail.error.DataErrors
import com.procurement.revision.infrastructure.web.dto.request.amendment.FindAmendmentIdsRequest

fun FindAmendmentIdsRequest.convert(): Result<FindAmendmentIdsParams, DataErrors>  =
    FindAmendmentIdsParams.tryCreate(
        status = status,
        relatedItems = relatedItems,
        cpid = cpid,
        relatesTo = relatesTo,
        type = type,
        ocid = ocid
    )


