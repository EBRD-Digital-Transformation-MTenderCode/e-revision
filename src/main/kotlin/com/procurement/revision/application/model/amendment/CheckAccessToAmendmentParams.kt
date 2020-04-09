package com.procurement.revision.application.model.amendment

import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.functional.Result.Companion.failure
import com.procurement.revision.domain.functional.asSuccess
import com.procurement.revision.domain.model.Cpid
import com.procurement.revision.domain.model.Ocid
import com.procurement.revision.domain.model.Owner
import com.procurement.revision.domain.model.Token
import com.procurement.revision.domain.model.amendment.AmendmentId
import com.procurement.revision.infrastructure.fail.error.DataErrors

class CheckAccessToAmendmentParams private constructor(
    val cpid: Cpid,
    val ocid: Ocid,
    val token: Token,
    val owner: Owner,
    val amendmentId: AmendmentId
) {
    companion object {
        fun tryCreate(
            cpid: String,
            ocid: String,
            token: String,
            owner: String,
            amendmentId: String
        ): Result<CheckAccessToAmendmentParams, DataErrors> {
            val cpidParsed = parseCpid(cpid)
                .doReturn { error -> return failure(error = error) }

            val ocidParsed = parseOcid(ocid)
                .doReturn { error -> return failure(error = error) }

            val amendmentIdParsed = parseAmendmentId(value = amendmentId, attributeName =  "amendmentId")
                .doReturn { error -> return failure(error = error) }

            val tokenParsed = parseToken(token)
                .doReturn { error -> return failure(error = error) }

            val ownerParsed = parseOwner(owner)
                .doReturn { error -> return failure(error = error) }

            return CheckAccessToAmendmentParams(
                cpid = cpidParsed,
                ocid = ocidParsed,
                token = tokenParsed,
                owner = ownerParsed,
                amendmentId = amendmentIdParsed
            ).asSuccess()
        }
    }
}