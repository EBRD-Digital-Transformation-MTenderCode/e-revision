package com.procurement.revision.infrastructure.model.dto

import com.procurement.revision.infrastructure.web.dto.ApiErrorResponse
import org.junit.jupiter.api.Test

class ApiErrorResponseTest : AbstractDTOTestBase<ApiErrorResponse>(ApiErrorResponse::class.java) {
    @Test
    fun fully() {
        testBindingAndMapping("json/dto/api_error_response.json")
    }
}
