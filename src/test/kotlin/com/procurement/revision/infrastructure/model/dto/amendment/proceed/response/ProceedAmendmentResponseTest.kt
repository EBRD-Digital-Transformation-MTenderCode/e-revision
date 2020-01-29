package com.procurement.revision.infrastructure.dto.amendment.respodescriptionnse

import com.procurement.revision.infrastructure.dto.amendment.ProceedAmendmentResponse
import com.procurement.revision.infrastructure.model.dto.AbstractDTOTestBase
import org.junit.jupiter.api.Test

class ProceedAmendmentResponseTest : AbstractDTOTestBase<ProceedAmendmentResponse>(ProceedAmendmentResponse::class.java) {

    @Test
    fun full() {
        testBindingAndMapping("json/dto/amendment/proceed/response/response_proceed_amendment_full.json")
    }

    @Test
    fun required_1() {
        testBindingAndMapping("json/dto/amendment/proceed/response/response_proceed_amendment_required_1.json")
    }

    @Test
    fun required_2() {
        testBindingAndMapping("json/dto/amendment/proceed/response/response_proceed_amendment_required_2.json")
    }
}
