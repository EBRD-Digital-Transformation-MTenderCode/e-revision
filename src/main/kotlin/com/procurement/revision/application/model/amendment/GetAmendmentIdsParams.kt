package com.procurement.revision.application.model.amendment

import com.procurement.revision.domain.enums.AmendmentRelatesTo
import com.procurement.revision.domain.enums.AmendmentStatus
import com.procurement.revision.domain.enums.AmendmentType
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.infrastructure.fail.error.DataErrors

data class GetAmendmentIdsParams private constructor(
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
        ): Result<GetAmendmentIdsParams, DataErrors> {
            val statusResult = status?.let { AmendmentStatus.tryFromString(it) }
            if (statusResult != null && statusResult.isFail) return Result.failure(DataErrors.UnknownValue("status"))

            val typeResult = type?.let { AmendmentType.tryFromString(it) }
            if (typeResult != null && typeResult.isFail) return Result.failure(DataErrors.UnknownValue("type"))

            val relatesToResult = relatesTo?.let { AmendmentRelatesTo.tryFromString(it) }
            if (relatesToResult != null && relatesToResult.isFail) return Result.failure(DataErrors.UnknownValue("relatesTo"))

            if (relatedItems != null && relatedItems.isEmpty()) Result.failure(DataErrors.EmptyArray("relatedItems"))

            return Result.success(
                GetAmendmentIdsParams(
                    ocid = ocid,
                    status = statusResult?.get,
                    type = typeResult?.get,
                    relatesTo = relatesToResult?.get,
                    cpid = cpid,
                    relatedItems = relatedItems?.toList().orEmpty()
                )
            )
        }
    }
}