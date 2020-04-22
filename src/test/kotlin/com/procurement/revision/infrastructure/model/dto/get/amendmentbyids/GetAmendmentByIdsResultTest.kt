package com.procurement.revision.infrastructure.model.dto.get.amendmentbyids

import com.procurement.revision.application.model.amendment.part.GetAmendmentByIdsResult
import com.procurement.revision.infrastructure.model.dto.AbstractDTOTestBase
import com.procurement.revision.infrastructure.web.dto.request.amendment.GetAmendmentByIdsRequest
import org.junit.jupiter.api.Test

class GetAmendmentByIdsResultTest : AbstractDTOTestBase<GetAmendmentByIdsResult>(GetAmendmentByIdsResult::class.java) {

    @Test
    fun full() {
        testBindingAndMapping(pathToJsonFile = "json/dto/get.amendmentbyids/get_amendment_by_ids_result_full.json")
    }
    @Test
    fun required_1() {
        testBindingAndMapping(pathToJsonFile = "json/dto/get.amendmentbyids/get_amendment_by_ids_result_required_1.json")
    }
    @Test
    fun required_2() {
        testBindingAndMapping(pathToJsonFile = "json/dto/get.amendmentbyids/get_amendment_by_ids_result_required_2.json")
    }
}