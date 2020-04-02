package com.procurement.revision.application.model.amendment.state

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
                return failure(DataErrors.Validation.EmptyArray(name = "amendmentIds"))

            val amendmentIdParsed = amendmentIds.mapResult { amendmentId -> parseAmendmentId(amendmentId) }
                .forwardResult { error -> return error }

            return GetMainPartOfAmendmentParams(
                cpid = cpidParsed,
                ocid = ocidParsed,
                amendmentIds = amendmentIdParsed
            ).asSuccess()
        }
    }
}