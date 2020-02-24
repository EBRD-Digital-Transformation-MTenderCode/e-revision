package com.procurement.revision.infrastructure.web.dto

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode
import com.procurement.revision.application.exception.ErrorException
import com.procurement.revision.domain.exception.EnumException
import com.procurement.revision.infrastructure.configuration.properties.GlobalProperties
import com.procurement.revision.infrastructure.utils.toObject
import java.time.LocalDateTime
import java.util.*

enum class CommandType(@JsonValue override val value: String) : Action {

    GET_AMENDMENTS_IDS("getAmendmentIds"),
    DATA_VALIDATION("dataValidation"),
    CREATE_AMENDMENT("createAmendment");

    companion object {
        private val elements: Map<String, CommandType> = values().associateBy { it.value.toUpperCase() }
        fun fromString(value: String): CommandType = elements[value.toUpperCase()]
            ?: throw EnumException(
                enumType = CommandType::class.java.canonicalName,
                value = value,
                values = values().joinToString { it.value }
            )
    }

    override fun toString() = value
}

fun errorResponse(exception: Exception, id: UUID = NaN, version: ApiVersion): ApiResponse =
    when (exception) {
        is ErrorException -> ApiFailResponse(
            id = id,
            version = version,
            result = listOf(
                ApiFailResponse.Error(
                    code = getFullErrorCode(exception.code),
                    description = exception.message!!
                )
            )
        )
        is EnumException -> ApiFailResponse(
            id = id,
            version = version,
            result = listOf(
                ApiFailResponse.Error(
                    code = getFullErrorCode(exception.code),
                    description = exception.message!!
                )
            )
        )
        else -> ApiIncidentResponse(
            id = id,
            version = version,
            result = createIncident("00.00", exception.message ?: "Internal server error.")
        )
    }

fun createIncident(code: String, message: String, metadata: Any? = null): ApiIncidentResponse.Incident {
    return ApiIncidentResponse.Incident(
        date = LocalDateTime.now(),
        id = UUID.randomUUID(),
        service = ApiIncidentResponse.Incident.Service(
            id = GlobalProperties.serviceId,
            version = GlobalProperties.App.apiVersion,
            name = GlobalProperties.serviceName
        ),
        errors = listOf(
            ApiIncidentResponse.Incident.Error(
                code = getFullErrorCode(code),
                description = message,
                metadata = metadata
            )
        )
    )
}

fun getFullErrorCode(code: String): String = "400.${GlobalProperties.serviceId}." + code

val NaN: UUID
    get() = UUID(0, 0)

fun JsonNode.getBy(parameter: String): JsonNode {
    val node = get(parameter)
    if (node == null || node is NullNode) throw IllegalArgumentException("$parameter is absent")
    return node
}

fun JsonNode.getId() = UUID.fromString(getBy("id").asText())
fun JsonNode.getVersion() = ApiVersion.valueOf(getBy("version").asText())
fun JsonNode.getAction() = CommandType.fromString(getBy("action").asText())
fun <T : Any> JsonNode.getParams(target: Class<T>) = getBy("params").toObject(target)
