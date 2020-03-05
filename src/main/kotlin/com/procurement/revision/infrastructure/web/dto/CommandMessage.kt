package com.procurement.revision.infrastructure.web.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode
import com.procurement.revision.domain.enums.EnumElementProvider
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.functional.asSuccess
import com.procurement.revision.domain.functional.bind
import com.procurement.revision.domain.util.extension.tryUUID
import com.procurement.revision.infrastructure.configuration.properties.GlobalProperties
import com.procurement.revision.infrastructure.fail.Fail
import com.procurement.revision.infrastructure.fail.error.DataErrors
import com.procurement.revision.infrastructure.utils.tryToObject
import java.time.LocalDateTime
import java.util.*

enum class CommandType(@JsonValue override val key: String) : Action, EnumElementProvider.Key {

    GET_AMENDMENTS_IDS("getAmendmentIds"),
    DATA_VALIDATION("dataValidation"),
    CREATE_AMENDMENT("createAmendment");

    override fun toString(): String = key

    companion object : EnumElementProvider<CommandType>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = CommandType.orThrow(name)
    }
}

fun generateResponseOnFailure(
    fails: List<Fail>,
    version: ApiVersion,
    id: UUID
): ApiResponse =
    when (fails[0]) {
        is Fail.Error ->
            if (fails[0] is DataErrors.Validation) {
                ApiDataErrorResponse(
                    version = version,
                    id = id,
                    result = fails.filterIsInstance<DataErrors.Validation>().map { dataError ->
                        ApiDataErrorResponse.Error(
                            code = getFullErrorCode(dataError.code),
                            description = dataError.description,
                            attributeName = dataError.name
                        )
                    }
                )
            } else {
                ApiFailResponse(
                    version = version,
                    id = id,
                    result = fails.filterIsInstance<Fail.Error>().map { error ->
                        ApiFailResponse.Error(
                            code = getFullErrorCode(error.code),
                            description = error.description
                        )
                    }
                )
            }
        is Fail.Incident ->
            ApiIncidentResponse(
                version = version,
                id = id,
                result = ApiIncidentResponse.Incident(
                    date = LocalDateTime.now(),
                    id = UUID.randomUUID(),
                    service = ApiIncidentResponse.Incident.Service(
                        id = GlobalProperties.service.id,
                        version = GlobalProperties.service.version,
                        name = GlobalProperties.service.name
                    ),
                    errors = fails.filterIsInstance<Fail.Incident>().map { incident ->
                        ApiIncidentResponse.Incident.Error(
                            code = getFullErrorCode(incident.code),
                            description = incident.description,
                            metadata = null
                        )
                    }
                )
            )
    }

fun getFullErrorCode(code: String): String = "${code}/${GlobalProperties.service.id}"

val NaN: UUID
    get() = UUID(0, 0)

fun JsonNode.tryGetAttribute(name: String): Result<JsonNode, DataErrors> {
    val node = get(name) ?: return Result.failure(
        DataErrors.Validation.MissingRequiredAttribute(name)
    )
    if (node is NullNode) return Result.failure(
        DataErrors.Validation.DataTypeMismatch(
            name = name,
            actualType = "null",
            expectedType = "not null"
        )
    )

    return Result.success(node)
}

fun JsonNode.tryGetVersion(): Result<ApiVersion, DataErrors> {
    val name = "version"
    return tryGetAttribute(name).bind {
        when (val result = ApiVersion.tryValueOf(it.asText())) {
            is Result.Success -> result
            is Result.Failure -> Result.failure(
                DataErrors.Validation.DataFormatMismatch(
                    name = name,
                    expectedFormat = "00.00.00",
                    actualValue = it.asText()
                )
            )
        }
    }
}

fun JsonNode.tryGetAction(): Result<CommandType, DataErrors> {
    val name = "action"
    return tryGetAttribute(name).bind { action ->
        CommandType.orNull(action.asText())?.asSuccess<CommandType, DataErrors>() ?: Result.failure(
            DataErrors.Validation.UnknownValue(
                name = name,
                actualValue = action.asText(),
                expectedValues = CommandType.allowedValues
            )
        )
    }
}

fun <T : Any> JsonNode.tryGetParams(target: Class<T>): Result<T, DataErrors> {
    val name = "params"
    return tryGetAttribute(name).bind {
        when (val result = it.tryToObject(target)) {
            is Result.Success -> result
            is Result.Failure -> Result.failure(
                DataErrors.Parsing("Error parsing '$name'")
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
                DataErrors.Validation.DataFormatMismatch(
                    name = name,
                    actualValue = it.asText(),
                    expectedFormat = "uuid"
                )
            )
        }
    }
}


