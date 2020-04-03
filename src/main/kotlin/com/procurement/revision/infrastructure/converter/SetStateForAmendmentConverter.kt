package com.procurement.revision.infrastructure.converter

import com.procurement.revision.application.model.amendment.state.SetStateForAmendmentParams
import com.procurement.revision.application.model.amendment.state.SetStateForAmendmentResult
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.model.amendment.Amendment
import com.procurement.revision.infrastructure.fail.error.DataErrors
import com.procurement.revision.infrastructure.web.dto.request.amendment.SetStateForAmendmentRequest

fun SetStateForAmendmentRequest.convert(): Result<SetStateForAmendmentParams, DataErrors> =
    SetStateForAmendmentParams.tryCreate(
        cpid = cpid,
        ocid = ocid,
        amendment = amendment.convert().forwardResult { error -> return error }
    )

fun SetStateForAmendmentRequest.Amendment.convert(): Result<SetStateForAmendmentParams.Amendment, DataErrors> =
    SetStateForAmendmentParams.Amendment.tryCreate(
        id = id,
        status = status
    )

fun Amendment.convertToSetStateForAmendmentResult() =
    SetStateForAmendmentResult(
        id = id,
        status = status
    )