package com.procurement.revision.application.model.amendment.state

import com.procurement.revision.application.model.amendment.parseAmendmentId
import com.procurement.revision.application.model.amendment.parseAmendmentStatus
import com.procurement.revision.application.model.amendment.parseCpid
import com.procurement.revision.application.model.amendment.parseOcid
import com.procurement.revision.domain.enums.AmendmentStatus
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.functional.asSuccess
import com.procurement.revision.domain.model.Cpid
import com.procurement.revision.domain.model.Ocid
import com.procurement.revision.domain.model.amendment.AmendmentId
import com.procurement.revision.infrastructure.fail.error.DataErrors

class SetStateForAmendmentParams private constructor(
    val cpid: Cpid,
    val ocid: Ocid,
    val amendment: Amendment
) {
    companion object {

        private val allowedStatuses = AmendmentStatus.allowedElements
            .filter { value ->
                when (value) {
                    AmendmentStatus.ACTIVE,
                    AmendmentStatus.CANCELLED -> true
                    AmendmentStatus.PENDING,
                    AmendmentStatus.WITHDRAWN -> false
                }
            }.toSet()

        fun tryCreate(
            cpid: String, ocid: String, amendment: Amendment
        ): Result<SetStateForAmendmentParams, DataErrors> {
            val cpidParsed = parseCpid(cpid)
                .orForwardFail { error -> return error }

            val ocidParsed = parseOcid(ocid)
                .orForwardFail { error -> return error }

            return SetStateForAmendmentParams(
                cpid = cpidParsed, ocid = ocidParsed, amendment = amendment
            ).asSuccess()
        }
    }

    class Amendment private constructor(val id: AmendmentId, val status: AmendmentStatus) {
        companion object {
            fun tryCreate(
                id: String, status: String
            ): Result<Amendment, DataErrors> {
                val idParsed = parseAmendmentId(value = id, attributeName = "amendment.id")
                    .orForwardFail { error -> return error }

                val statusParsed = parseAmendmentStatus(
                    status = status,
                    attributeName = "amendment.status",
                    allowedStatuses = allowedStatuses
                ).orForwardFail { error -> return error }

                return Amendment(id = idParsed, status = statusParsed).asSuccess()
            }
        }
    }
}
