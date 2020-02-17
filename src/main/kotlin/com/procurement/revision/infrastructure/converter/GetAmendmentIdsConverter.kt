package com.procurement.revision.infrastructure.converter

import com.procurement.revision.application.exception.ErrorException
import com.procurement.revision.application.exception.ErrorType
import com.procurement.revision.application.model.amendment.GetAmendmentIdsParams
import com.procurement.revision.domain.enums.AmendmentRelatesTo
import com.procurement.revision.domain.enums.AmendmentStatus
import com.procurement.revision.domain.enums.AmendmentType
import com.procurement.revision.infrastructure.web.dto.request.amendment.GetAmendmentIdsRequest
import com.procurement.revision.lib.errorIfEmpty

fun GetAmendmentIdsRequest.convert(): GetAmendmentIdsParams {
    return GetAmendmentIdsParams(
        status = status?.let { AmendmentStatus.fromString(it) },
        type = type?.let { AmendmentType.fromString(it) },
        relatesTo = relatesTo?.let { AmendmentRelatesTo.fromString(it) },
        relatedItems = relatedItems.errorIfEmpty {
            ErrorException(
                error = ErrorType.IS_EMPTY,
                message = "The amendment with cpid '${cpid}' contains empty list of relatedItems."
            )
        }
            ?.toList()
            .orEmpty(),
        cpid = cpid,
        ocid = ocid
    )
}
