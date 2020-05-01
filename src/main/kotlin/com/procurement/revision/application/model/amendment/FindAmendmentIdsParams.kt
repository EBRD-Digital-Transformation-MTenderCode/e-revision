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
import com.procurement.revision.lib.toSetBy

class FindAmendmentIdsParams private constructor(
    val status: AmendmentStatus?,
    val type: AmendmentType?,
    val relatesTo: AmendmentRelatesTo?,
    val relatedItems: List<String>,
    val cpid: Cpid,
    val ocid: Ocid
) {
    companion object {
        private val allowedStatuses = AmendmentStatus.values()
            .filter { value ->
                when (value) {
                    AmendmentStatus.PENDING,
                    AmendmentStatus.CANCELLED,
                    AmendmentStatus.ACTIVE -> true
                    AmendmentStatus.WITHDRAWN -> false
                }
            }.toSetBy { it.key }

        private val allowedTypes = AmendmentType.values()
            .filter { value ->
                when (value) {
                    AmendmentType.CANCELLATION -> true
                    AmendmentType.TENDER_CHANGE -> false
                }
            }.toSetBy { it.key }

        private val allowedRelatesTo = AmendmentRelatesTo.values()
            .filter { value ->
                when (value) {
                    AmendmentRelatesTo.LOT,
                    AmendmentRelatesTo.TENDER -> true
                    AmendmentRelatesTo.CAN -> false
                }
            }.toSetBy { it.key }

        fun tryCreate(
            status: String?,
            type: String?,
            relatesTo: String?,
            relatedItems: List<String>?,
            cpid: String,
            ocid: String
        ): Result<FindAmendmentIdsParams, DataErrors> {
            val statusParsed = status
                ?.let {
                    AmendmentStatus.orNull(it)
                        ?.takeIf { it.key in allowedStatuses }
                        ?: return failure(
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
                        ?.takeIf { it.key in allowedTypes }
                        ?: return failure(
                            DataErrors.Validation.UnknownValue(
                                name = "type",
                                actualValue = type,
                                expectedValues = allowedTypes
                            )
                        )
                }

            val relatesToParsed = relatesTo
                ?.let {
                    AmendmentRelatesTo.orNull(it)
                        ?.takeIf { it.key in allowedRelatesTo }
                        ?: return failure(
                            DataErrors.Validation.UnknownValue(
                                name = "relatesTo",
                                expectedValues = allowedRelatesTo,
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
                FindAmendmentIdsParams(
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