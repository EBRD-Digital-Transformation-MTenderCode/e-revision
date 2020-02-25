package com.procurement.revision.application.repository

import com.procurement.revision.domain.model.amendment.Amendment
import com.procurement.revision.domain.model.amendment.AmendmentId

interface AmendmentRepository {
    fun findBy(cpid: String, ocid: String): List<Amendment>
    fun findBy(cpid: String, ocid: String, id: AmendmentId): Amendment?
    fun saveNewAmendment(cpid: String, ocid: String, amendment: Amendment): Boolean
}
