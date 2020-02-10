package com.procurement.revision.infrastructure.web.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import java.util.*

class ApiResponse2(
    @field:JsonProperty("version") @param:JsonProperty("version") val version: ApiVersion,
    @field:JsonProperty("id") @param:JsonProperty("id") val id: UUID,
    @field:JsonProperty("status") @param:JsonProperty("status") val status: ResponseStatus,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("result") @param:JsonProperty("result") val result: Any?
)

enum class ResponseStatus(private val value: String) {

    SUCCESS("success"),
    FAILURE("failure"),
    INCIDENT("incident");

    @JsonValue
    fun value(): String {
        return this.value
    }

    override fun toString(): String {
        return this.value
    }
}

class ApiErrorResult2(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
    @field:JsonProperty("details") @param:JsonProperty("details") val details: String
)