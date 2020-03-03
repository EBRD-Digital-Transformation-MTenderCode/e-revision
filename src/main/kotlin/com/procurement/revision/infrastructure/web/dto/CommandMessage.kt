package com.procurement.revision.infrastructure.web.dto

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode
import com.procurement.revision.application.exception.ErrorException
import com.procurement.revision.domain.exception.EnumException
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.functional.bind
import com.procurement.revision.domain.util.extension.tryUUID
import com.procurement.revision.infrastructure.configuration.properties.GlobalProperties
import com.procurement.revision.infrastructure.fail.Fail
import com.procurement.revision.infrastructure.fail.error.DataErrors
import com.procurement.revision.infrastructure.fail.error.EnumError
import com.procurement.revision.infrastructure.utils.tryToObject
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

        fun tryFromString(value: String): Result<CommandType, EnumError> =
            elements[value.toUpperCase()]
                ?.let { Result.success(it) }
                ?: Result.failure(EnumError(
                    enumType = CommandType::class.java.canonicalName,
                    value = value,
                    values = values().joinToString { it.value }
                )
                )
    }

    override fun toString() = value
}

fun errorResponse(
    exception: Exception,
    id: UUID = NaN,
    version: ApiVersion = GlobalProperties.App.apiVersion
): ApiResponse =
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

fun generateResponseOnFailure(
    fail: Fail,
    version: ApiVersion,
    id: UUID
): ApiResponse =
    when (fail) {
        is Fail.Error ->
            ApiFailResponse(
                version = version,
                id = id,
                result = listOf(
                    ApiFailResponse.Error(
                        code = getFullErrorCode(fail.code),
                        description = fail.description
                    )
                )
            )
        is Fail.Incident ->
            ApiIncidentResponse(
                version = version,
                id = id,
                result = ApiIncidentResponse.Incident(
                    date = LocalDateTime.now(),
                    id = UUID.randomUUID(),
                    service = ApiIncidentResponse.Incident.Service(
                        id = GlobalProperties.serviceId,
                        version = GlobalProperties.App.apiVersion,
                        name = GlobalProperties.serviceName
                    ),
                    errors = listOf(
                        ApiIncidentResponse.Incident.Error(
                            code = getFullErrorCode(fail.code),
                            description = fail.description,
                            metadata = null
                        )
                    )
                )
            )
    }

fun getFullErrorCode(code: String): String = "400.${GlobalProperties.serviceId}." + code

val NaN: UUID
    get() = UUID(0, 0)

fun JsonNode.tryGetAttribute(name: String): Result<JsonNode, DataErrors> {
    val node = get(name)
    if (node == null || node is NullNode) return Result.failure(
        DataErrors.MissingRequiredAttribute("$name is absent")
    )
    return Result.success(node)
}

fun JsonNode.tryGetVersion(): Result<ApiVersion, DataErrors> {
    val name = "version"
    return tryGetAttribute(name).bind {
        when (val result = ApiVersion.tryValueOf(it.asText())) {
            is Result.Success -> result
            is Result.Failure -> Result.failure(
                DataErrors.DataFormatMismatch(name)
            )
        }
    }
}

fun JsonNode.tryGetAction(): Result<CommandType, DataErrors> {
    val name = "action"
    return tryGetAttribute(name).bind { action ->
        when (val result = CommandType.tryFromString(action.asText())) {
            is Result.Success -> result
            is Result.Failure -> Result.failure(
                DataErrors.UnknownValue(name)
            )
        }
    }
}

fun <T : Any> JsonNode.tryGetParams(target: Class<T>): Result<T, DataErrors> {
    val name = "params"
    return tryGetAttribute(name).bind {
        when (val result = it.tryToObject(target)) {
            is Result.Success -> result
            is Result.Failure -> Result.failure(
                DataErrors.DataFormatMismatch(name)
            )
        }
    }
}

fun JsonNode.tryGetId(): Result<UUID, DataErrors> {
    val name = "id"
    return tryGetAttribute(name).bind {
        when (val result = it.asText().tryUUID()) {
            is Result.Success -> result
            is Result.Failure -> Result.failure(
                DataErrors.DataFormatMismatch(name)
            )
        }
    }
}


