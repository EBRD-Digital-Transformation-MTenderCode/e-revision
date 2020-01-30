package com.procurement.revision.infrastructure.web.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.revision.infrastructure.bind.apiversion.ApiVersionDeserializer
import com.procurement.revision.infrastructure.bind.apiversion.ApiVersionSerializer

sealed class ApiResponse {
    abstract val id: String
    abstract val version: ApiVersion
}

class ApiErrorResponse(
    @field:JsonProperty("id") @param:JsonProperty("id") override val id: String,
    @field:JsonProperty("version") @param:JsonProperty("version") override val version: ApiVersion,
    @field:JsonProperty("errors") @param:JsonProperty("errors") val errors: List<Error>
) : ApiResponse() {
    data class Error(
        @field:JsonProperty("code") @param:JsonProperty("code") val code: String,
        @field:JsonProperty("description") @param:JsonProperty("description") val description: String
    )
}

class ApiSuccessResponse(
    @field:JsonProperty("id") @param:JsonProperty("id") override val id: String,
    @field:JsonProperty("version") @param:JsonProperty("version") override val version: ApiVersion,
    @field:JsonProperty("data") @param:JsonProperty("data") val data: Any
) : ApiResponse()
