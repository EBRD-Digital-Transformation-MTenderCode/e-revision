package com.procurement.revision.infrastructure.web.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.JsonNode
import com.procurement.revision.application.exception.ErrorException
import com.procurement.revision.domain.exception.EnumException
import com.procurement.revision.infrastructure.configuration.properties.GlobalProperties
import java.util.*

data class Command2Message constructor(
    @field:JsonProperty("version") @param:JsonProperty("version") val version: ApiVersion,
    @field:JsonProperty("id") @param:JsonProperty("id") val id: UUID,
    @field:JsonProperty("action") @param:JsonProperty("action") val action: Command2Type,
    @field:JsonProperty("params") @param:JsonProperty("params") val params: JsonNode

)

enum class Command2Type(private val value: String) {

    GET_AMENDMENTS_IDS("getAmendmentIds");

    @JsonValue
    fun value(): String {
        return this.value
    }

    override fun toString(): String {
        return this.value
    }
}

fun errorResponse2(exception: Exception, id: UUID, version: ApiVersion, status: ResponseStatus): ApiResponse2 =
    when (exception) {
        is ErrorException -> ApiResponse2(
            id = id,
            version = version,
            status = status,
            result = getApiErrorResult2(code = exception.code, message = exception.message!!)
        )
        is EnumException -> ApiResponse2(
            id = id,
            version = version,
            status = status,
            result = getApiErrorResult2(code = exception.code, message = exception.message!!)
        )
        else -> ApiResponse2(
            id = id,
            version = version,
            status = status,
            result = getApiErrorResult2(code = "00.00", message = exception.message ?: "Internal server error.")
        )
    }

private fun getApiErrorResult2(code: String, message: String): List<ApiErrorResult2> =
    listOf(
        ApiErrorResult2(
            id = getFullErrorCode(code),
            details = message
        )
    )

private fun getFullErrorCode(code: String): String = "400.${GlobalProperties.serviceId}." + code

val NaN: UUID
    get() = UUID(0, 0)



