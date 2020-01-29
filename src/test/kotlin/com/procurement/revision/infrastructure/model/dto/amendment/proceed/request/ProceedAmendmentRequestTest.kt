package com.procurement.revision.infrastructure.dto.amendment.request

import com.procurement.revision.infrastructure.dto.amendment.ProceedAmendmentRequest
import com.procurement.revision.infrastructure.model.dto.AbstractDTOTestBase
import org.junit.jupiter.api.Test

class ProceedAmendmentRequestTest : AbstractDTOTestBase<ProceedAmendmentRequest>(ProceedAmendmentRequest::class.java) {

    @Test
    fun full() {
        testBindingAndMapping("json/dto/amendment/proceed/request/request_proceed_amendment_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/dto/amendment/proceed/request/request_proceed_amendment_required_1.json")
    }
}
