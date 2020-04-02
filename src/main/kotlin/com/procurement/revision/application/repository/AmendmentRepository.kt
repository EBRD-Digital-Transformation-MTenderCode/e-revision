package com.procurement.revision.application.repository

import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.model.Cpid
import com.procurement.revision.domain.model.Ocid
import com.procurement.revision.domain.model.amendment.Amendment
import com.procurement.revision.domain.model.amendment.AmendmentId
import com.procurement.revision.infrastructure.fail.Fail

interface AmendmentRepository {
    fun findBy(cpid: Cpid, ocid: Ocid): Result<List<Amendment>, Fail.Incident>
    fun findBy(cpid: Cpid, ocid: Ocid, id: AmendmentId): Result<Amendment?, Fail.Incident>
    fun findBy(cpid: Cpid, ocid: Ocid, ids: List<AmendmentId>): Result<List<Amendment>, Fail.Incident>
    fun saveNewAmendment(cpid: Cpid, ocid: Ocid, amendment: Amendment): Result<Boolean, Fail.Incident>
}
