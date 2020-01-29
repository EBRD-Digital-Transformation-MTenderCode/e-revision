package com.procurement.revision.infrastructure.model.dto

import com.procurement.revision.infrastructure.web.dto.ApiSuccessResponse
import org.junit.jupiter.api.Test

class ApiSuccessResponseTest : AbstractDTOTestBase<ApiSuccessResponse>(ApiSuccessResponse::class.java) {
    @Test
    fun emptyData() {
        testBindingAndMapping("json/dto/api_success_response_with_empty_data.json")
    }

    @Test
    fun noEmptyData() {
        testBindingAndMapping("json/dto/api_success_response_with_data.json")
    }
}
