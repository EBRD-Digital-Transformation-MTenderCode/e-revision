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
        ): Result<GetAmendmentIdsParams, DataErrors> {

            val statusParsed = status
                ?.let {
                    AmendmentStatus.orNull(it)
                        ?: return failure(
                            DataErrors.Validation.UnknownValue(
                                name = "status",
                                expectedValues = AmendmentStatus.allowedValues,
                                actualValue = status
                            )
                        )
                }

            val typeParsed = type
                ?.let {
                    AmendmentType.orNull(it)
                        ?: return failure(
                            DataErrors.Validation.UnknownValue(
                                name = "type",
                                actualValue = type,
                                expectedValues = AmendmentType.allowedValues
                            )
                        )
                }

            val relatesToParsed = relatesTo
                ?.let {
                    AmendmentRelatesTo.orNull(it)
                        ?: return failure(
                            DataErrors.Validation.UnknownValue(
                                name = "relatesTo",
                                expectedValues = AmendmentRelatesTo.allowedValues,
                                actualValue = relatesTo
                            )
                        )
                }

            if (relatedItems != null && relatedItems.isEmpty())
                return failure(DataErrors.Validation.EmptyArray("relatedItems"))
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