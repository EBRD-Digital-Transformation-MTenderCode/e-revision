package com.procurement.revision.application.model.amendment

import com.procurement.revision.domain.enums.AmendmentRelatesTo
import com.procurement.revision.domain.enums.AmendmentStatus
import com.procurement.revision.domain.enums.AmendmentType
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.functional.Result.Companion.failure
import com.procurement.revision.domain.functional.Result.Companion.success
import com.procurement.revision.infrastructure.fail.error.DataErrors

class GetAmendmentIdsParams private constructor(
    val status: AmendmentStatus?,
    val type: AmendmentType?,
    val relatesTo: AmendmentRelatesTo?,
    val relatedItems: List<String>,
    val cpid: String,
    val ocid: String
) {
    companion object {
        fun tryCreate(
            status: String?,
            type: String?,
            relatesTo: String?,
            relatedItems: List<String>?,
            cpid: String,
            ocid: String
        ): Result<GetAmendmentIdsParams, List<DataErrors>> {

            val statusParsed = status
                ?.let { AmendmentStatus.tryOf(it) }
                ?.doOnError { return failure(listOf(DataErrors.UnknownValue("status"))) }
                ?.get

            val typeParsed = type
                ?.let { AmendmentType.tryOf(it) }
                ?.doOnError { return failure(listOf(DataErrors.UnknownValue("type"))) }
                ?.get

            val relatesToParsed = relatesTo
                ?.let { AmendmentRelatesTo.tryOf(it) }
                ?.doOnError { return failure(listOf(DataErrors.UnknownValue("relatesTo"))) }
                ?.get

            if (relatedItems != null && relatedItems.isEmpty())
                return failure(listOf(DataErrors.EmptyArray("relatedItems")))
            val relatedItemsTransformed = relatedItems?.toList().orEmpty()

            return success(
                GetAmendmentIdsParams(
                    ocid = ocid,
                    status = statusParsed,
                    type = typeParsed,
                    relatesTo = relatesToParsed,
                    cpid = cpid,
                    relatedItems = relatedItemsTransformed
                )
            )
        }
    }
}