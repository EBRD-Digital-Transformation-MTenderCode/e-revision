package com.procurement.revision.infrastructure.web.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import java.time.LocalDateTime
import java.util.*

sealed class ApiResponse(
    @field:JsonProperty("version") @param:JsonProperty("version") val version: ApiVersion,
    @field:JsonProperty("id") @param:JsonProperty("id") val id: UUID,
    @field:JsonProperty("result") @param:JsonProperty("result") val result: Any?
) {
    abstract val status: ResponseStatus
}

class ApiSuccessResponse(
    version: ApiVersion, id: UUID,
    @JsonInclude(JsonInclude.Include.NON_EMPTY) result: Any? = null
) : ApiResponse(
    version = version,
    id = id,
    result = result
) {
    @field:JsonProperty("status")
    override val status: ResponseStatus = ResponseStatus.SUCCESS
}

class ApiFailResponse(version: ApiVersion, id: UUID, result: List<Error>) :
    ApiResponse(version = version, id = id, result = result) {

    @field:JsonProperty("status")
    override val status: ResponseStatus = ResponseStatus.ERROR

    class Error(val code: String, val description: String?)
}

class ApiIncidentResponse(version: ApiVersion, id: UUID, result: Incident) :
    ApiResponse(version = version, id = id, result = result) {

    @field:JsonProperty("status")
    override val status: ResponseStatus = ResponseStatus.INCIDENT

    class Incident(val id: UUID, val date: LocalDateTime, val service: Service, val errors: List<Error>) {
        class Service(val id: String, val name: String, val version: ApiVersion)
        class Error(val code: String, val description: String, val metadata: Any?)
    }
}

class ApiDataErrorResponse(
    version: ApiVersion, id: UUID, result: List<Error>
) : ApiResponse(version = version, result = result, id = id) {
    @field:JsonProperty("status")
    override val status: ResponseStatus = ResponseStatus.ERROR

    class Error(val code: String?, val description: String?, val attributeName: String)
}

enum class ResponseStatus(private val value: String) {

    SUCCESS("success"),
    ERROR("error"),
    INCIDENT("incident");

    @JsonValue
    fun value(): String {
        return this.value
    }

    override fun toString(): String {
        return this.value
    }
}