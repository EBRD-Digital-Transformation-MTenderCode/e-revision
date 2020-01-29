package com.procurement.revision.application.repository

import com.procurement.revision.domain.model.Amendment
import com.procurement.revision.domain.model.AmendmentId

interface AmendmentRepository {
    fun findBy(cpid: String): List<Amendment>
    fun findBy(cpid: String, id: AmendmentId): Amendment?
    fun saveNewAmendment(cpid: String, amendment: Amendment): Boolean
}
