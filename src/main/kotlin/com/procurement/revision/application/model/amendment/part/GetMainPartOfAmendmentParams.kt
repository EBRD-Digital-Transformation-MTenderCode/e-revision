package com.procurement.revision.application.model.amendment.part

import com.procurement.revision.application.model.amendment.parseAmendmentId
import com.procurement.revision.application.model.amendment.parseCpid
import com.procurement.revision.application.model.amendment.parseOcid
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.functional.Result.Companion.failure
import com.procurement.revision.domain.functional.asSuccess
import com.procurement.revision.domain.model.Cpid
import com.procurement.revision.domain.model.Ocid
import com.procurement.revision.domain.model.amendment.AmendmentId
import com.procurement.revision.domain.util.extension.mapResult
import com.procurement.revision.infrastructure.fail.error.DataErrors

class GetMainPartOfAmendmentParams private constructor(
    val cpid: Cpid,
    val ocid: Ocid,
    val amendmentIds: List<AmendmentId>
) {
    companion object {
        private const val AMENDMENT_IDS_ATTRIBUTE_NAME = "amendmentIds"

        fun tryCreate(
            cpid: String,
            ocid: String,
            amendmentIds: List<String>
        ): Result<GetMainPartOfAmendmentParams, DataErrors> {
            val cpidParsed = parseCpid(cpid)
                .doReturn { error -> return failure(error = error) }

            val ocidParsed = parseOcid(ocid)
                .doReturn { error -> return failure(error = error) }

            if (amendmentIds.isEmpty())
                return failure(DataErrors.Validation.EmptyArray(name = AMENDMENT_IDS_ATTRIBUTE_NAME))

            val amendmentIdParsed = amendmentIds.mapResult { amendmentId ->
                parseAmendmentId(value = amendmentId, attributeName = AMENDMENT_IDS_ATTRIBUTE_NAME)
            }
                .orForwardFail { error -> return error }

            val duplicateIds = amendmentIdParsed
                .groupingBy { it }
                .eachCount()
                .filter { it.value > 1 }
                .map { it.key }

            if (duplicateIds.isNotEmpty())
                return failure(
                    DataErrors.Validation.UniquenessDataMismatch(
                        value = duplicateIds.first().toString(),
                        name = AMENDMENT_IDS_ATTRIBUTE_NAME
                    )
                )

            return GetMainPartOfAmendmentParams(
                cpid = cpidParsed,
                ocid = ocidParsed,
                amendmentIds = amendmentIdParsed
            ).asSuccess()
        }
    }
}