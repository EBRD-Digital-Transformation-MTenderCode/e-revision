package com.procurement.revision.infrastructure.model.dto.get.amendmentbyids

import com.procurement.revision.infrastructure.model.dto.AbstractDTOTestBase
import com.procurement.revision.infrastructure.web.dto.request.amendment.GetAmendmentByIdsRequest
import org.junit.jupiter.api.Test

class GetAmendmentByIdsRequestTest : AbstractDTOTestBase<GetAmendmentByIdsRequest>(GetAmendmentByIdsRequest::class.java) {

    @Test
    fun full() {
        testBindingAndMapping(pathToJsonFile = "json/dto/get.amendmentbyids/get_amendment_by_ids_request_full.json")
    }
}