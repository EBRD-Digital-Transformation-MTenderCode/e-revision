package com.procurement.revision.infrastructure.converter

import com.procurement.revision.application.model.amendment.CheckAccessToAmendmentParams
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.infrastructure.fail.error.DataErrors
import com.procurement.revision.infrastructure.web.dto.request.amendment.CheckAccessToAmendmentRequest

fun CheckAccessToAmendmentRequest.convert(): Result<CheckAccessToAmendmentParams, DataErrors> =
    CheckAccessToAmendmentParams.tryCreate(
        cpid = cpid,
        ocid = ocid,
        amendmentId = amendmentId,
        owner = owner,
        token = token
    )


