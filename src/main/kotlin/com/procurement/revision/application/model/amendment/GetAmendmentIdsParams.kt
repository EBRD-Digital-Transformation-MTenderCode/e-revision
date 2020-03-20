package com.procurement.revision.application.model.amendment

import com.procurement.revision.domain.enums.AmendmentRelatesTo
import com.procurement.revision.domain.enums.AmendmentStatus
import com.procurement.revision.domain.enums.AmendmentType
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.functional.Result.Companion.failure
import com.procurement.revision.domain.functional.Result.Companion.success
import com.procurement.revision.domain.model.Cpid
import com.procurement.revision.domain.model.Ocid
import com.procurement.revision.infrastructure.fail.error.DataErrors

class GetAmendmentIdsParams private constructor(
    val status: AmendmentStatus?,
    val type: AmendmentType?,
    val relatesTo: AmendmentRelatesTo?,
    val relatedItems: List<String>,
    val cpid: Cpid,
    val ocid: Ocid
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

            val allowedStatuses = AmendmentStatus.values().filter { value ->
                when (value) {
                    AmendmentStatus.PENDING -> true
                    AmendmentStatus.CANCELLED,
                    AmendmentStatus.ACTIVE,
                    AmendmentStatus.WITHDRAWN -> false
                }
            }.map { it.key }

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
                }?.also { amendmentStatus ->
                    if (amendmentStatus.key !in allowedStatuses)
                        return failure(
                            DataErrors.Validation.UnknownValue(
                                name = "status",
                                expectedValues = allowedStatuses,
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

            val cpidParsed = parseCpid(cpid)
                .doReturn { error -> return failure(error = error) }

            val ocidParsed = parseOcid(ocid)
                .doReturn { error -> return failure(error = error) }

            return success(
                GetAmendmentIdsParams(
                    ocid = ocidParsed,
                    status = statusParsed,
                    type = typeParsed,
                    relatesTo = relatesToParsed,
                    cpid = cpidParsed,
                    relatedItems = relatedItemsTransformed
                )
            )
        }
    }
}