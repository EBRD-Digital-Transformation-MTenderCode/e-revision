package com.procurement.revision.infrastructure.web.dto

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode
import com.procurement.revision.application.exception.ErrorException
import com.procurement.revision.domain.exception.EnumException
import com.procurement.revision.infrastructure.configuration.properties.GlobalProperties
import java.time.LocalDateTime
import java.util.*

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

fun errorResponse2(exception: Exception, id: UUID = NaN, version: ApiVersion): ApiResponse2 =
    when (exception) {
        is ErrorException -> ApiFailResponse2(
            id = id,
            version = version,
            result = listOf(
                ApiFailResponse2.Error(
                    code = getFullErrorCode(exception.code),
                    description = exception.message!!
                )
            )
        )
        is EnumException -> ApiFailResponse2(
            id = id,
            version = version,
            result = listOf(
                ApiFailResponse2.Error(
                    code = getFullErrorCode(exception.code),
                    description = exception.message!!
                )
            )
        )
        else -> ApiIncidentResponse2(
            id = id,
            version = version,
            result = createIncident("00.00", exception.message ?: "Internal server error.")
        )
    }

fun createIncident(code: String, message: String, metadata: Any? = null): ApiIncidentResponse2.Incident {
    return ApiIncidentResponse2.Incident(
        date = LocalDateTime.now(),
        id = UUID.randomUUID(),
        service = ApiIncidentResponse2.Incident.Service(
            id = GlobalProperties.serviceId,
            version = GlobalProperties.App.apiVersion,
            name = GlobalProperties.serviceName
        ),
        errors = listOf(
            ApiIncidentResponse2.Incident.Error(
                code = getFullErrorCode(code),
                description = message,
                metadata = metadata
            )
        )
    )
}

private fun getFullErrorCode(code: String): String = "400.${GlobalProperties.serviceId}." + code

val NaN: UUID
    get() = UUID(0, 0)

fun JsonNode.getBy(parameter: String): JsonNode{
    val node = get(parameter)
    if(node == null || node is NullNode)  throw IllegalArgumentException("$parameter is absent")
    return node
}

fun JsonNode.getId() = UUID.fromString(getBy("id").asText())
fun JsonNode.getVersion() = ApiVersion.valueOf(getBy("version").asText())
fun JsonNode.getAction() = getBy("action").asText()
