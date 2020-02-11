package com.procurement.revision.infrastructure.model.dto.amendment.proceed

import com.procurement.revision.infrastructure.model.dto.AbstractDTOTestBase
import com.procurement.revision.infrastructure.web.dto.ApiResponse2
import com.procurement.revision.infrastructure.web.dto.ApiVersion
import com.procurement.revision.infrastructure.web.dto.ResponseStatus
import com.procurement.revision.json.JsonMapper
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.util.*

class ApiResponse2Test : AbstractDTOTestBase<ApiResponse2>(ApiResponse2::class.java) {
    @Test
    fun fullData() {
        testBindingAndMapping("json/dto/api_response.json")
    }

    @Test
    fun correctResultTest(){
        val apiResponse2 = ApiResponse2(version = ApiVersion(1,2,3),
                                        status = ResponseStatus.SUCCESS,
                                        result = listOf(UUID.randomUUID()),
                                        id = UUID.randomUUID())

        val tree = JsonMapper.mapper.valueToTree<com.fasterxml.jackson.databind.JsonNode>(apiResponse2)
        val result = tree.get("result")
        assertNotNull(result)
    }

    @Test
    fun emptyResultTest(){
        val apiResponse2 = ApiResponse2(version = ApiVersion(1,2,3),
                                        status = ResponseStatus.SUCCESS,
                                        result = emptyList<String>(),
                                        id = UUID.randomUUID())

        val tree = JsonMapper.mapper.valueToTree<com.fasterxml.jackson.databind.JsonNode>(apiResponse2)
        // If result is actually present in JsonNode but doesn't have value, NullNode is returned. Otherwise null is returned
        val result = tree.get("result")
        assertNull(result)
    }



    @Test
    fun nullResultTest(){
        val apiResponse2 = ApiResponse2(version = ApiVersion(1,2,3),
                                        status = ResponseStatus.SUCCESS,
                                        result = null,
                                        id = UUID.randomUUID())

        val tree = JsonMapper.mapper.valueToTree<com.fasterxml.jackson.databind.JsonNode>(apiResponse2)
        // If result is actually present in JsonNode but doesn't have value, NullNode is returned. Otherwise null is returned
        val result = tree.get("result")
        assertNull(result)
    }
}
