package com.procurement.revision.application.repository

import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.model.amendment.Amendment
import com.procurement.revision.domain.model.amendment.AmendmentId
import com.procurement.revision.infrastructure.fail.Fail

interface AmendmentRepository {
    fun findBy(cpid: String, ocid: String): Result<List<Amendment>, Fail>
    fun findBy(cpid: String, ocid: String, id: AmendmentId): Result<Amendment?, Fail>
    fun saveNewAmendment(cpid: String, ocid: String, amendment: Amendment): Result<Boolean, Fail>
}
