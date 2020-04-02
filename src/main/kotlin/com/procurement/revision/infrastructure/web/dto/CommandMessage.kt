package com.procurement.revision.infrastructure.web.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.JsonNode
import com.procurement.revision.application.service.Logger
import com.procurement.revision.domain.enums.EnumElementProvider
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.functional.bind
import com.procurement.revision.domain.util.extension.nowDefaultUTC
import com.procurement.revision.domain.util.extension.toListOrEmpty
import com.procurement.revision.domain.util.extension.tryUUID
import com.procurement.revision.infrastructure.configuration.properties.GlobalProperties
import com.procurement.revision.infrastructure.extension.tryGetAttribute
import com.procurement.revision.infrastructure.extension.tryGetAttributeAsEnum
import com.procurement.revision.infrastructure.extension.tryGetTextAttribute
import com.procurement.revision.infrastructure.fail.Fail
import com.procurement.revision.infrastructure.fail.error.BadRequest
import com.procurement.revision.infrastructure.fail.error.DataErrors
import com.procurement.revision.infrastructure.fail.error.ValidationError
import com.procurement.revision.infrastructure.utils.tryToNode
import com.procurement.revision.infrastructure.utils.tryToObject
import java.util.*

enum class CommandType(@JsonValue override val key: String) : Action, EnumElementProvider.Key {

    GET_AMENDMENTS_IDS("getAmendmentIds"),
    DATA_VALIDATION("dataValidation"),
    CREATE_AMENDMENT("createAmendment"),
    CHECK_ACCESS_TO_AMENDMENT("CheckAccessToAmendment"),
    GET_MAIN_PART_OF_AMENDMENT_BY_IDS("getMainPartOfAmendmentByIds");

    override fun toString(): String = key

    companion object : EnumElementProvider<CommandType>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = CommandType.orThrow(name)
    }
}

fun generateResponseOnFailure(
    fail: Fail,
    version: ApiVersion,
    id: UUID,
    logger: Logger
): ApiResponse {
    fail.logging(logger)
    return when (fail) {
        is Fail.Error -> {
            when (fail) {
                is DataErrors.Validation ->
                    generateDataErrorResponse(id = id, version = version, dataError = fail)
                is ValidationError ->
                    generateValidationErrorResponse(id = id, version = version, validationError = fail)
                else -> generateErrorResponse(id = id, version = version, error = fail)
            }
        }
        is Fail.Incident -> generateIncidentResponse(id = id, version = version, incident = fail)
    }
}

private fun generateDataErrorResponse(
    dataError: DataErrors.Validation, version: ApiVersion, id: UUID
) =
    ApiErrorResponse(
        version = version,
        id = id,
        result = listOf(
            ApiErrorResponse.Error(
                code = getFullErrorCode(dataError.code),
                description = dataError.description,
                details = ApiErrorResponse.Error.Detail
                    .tryCreateOrNull(name = dataError.name).toListOrEmpty()

            )
        )
    )

private fun generateValidationErrorResponse(
    validationError: ValidationError, version: ApiVersion, id: UUID
) =
    ApiErrorResponse(
        version = version,
        id = id,
        result = listOf(
            ApiErrorResponse.Error(
                code = getFullErrorCode(validationError.code),
                description = validationError.description,
                details = ApiErrorResponse.Error.Detail
                    .tryCreateOrNull(id = validationError.entityId).toListOrEmpty()

            )
        )
    )

private fun generateErrorResponse(version: ApiVersion, id: UUID, error: Fail.Error) =
    ApiErrorResponse(
        version = version,
        id = id,
        result = listOf(
            ApiErrorResponse.Error(
                code = getFullErrorCode(error.code),
                description = error.description
            )
        )
    )

private fun generateIncidentResponse(incident: Fail.Incident, version: ApiVersion, id: UUID) =
    ApiIncidentResponse(
        version = version,
        id = id,
        result = ApiIncidentResponse.Incident(
            date = nowDefaultUTC(),
            id = UUID.randomUUID(),
            service = ApiIncidentResponse.Incident.Service(
                id = GlobalProperties.service.id,
                version = GlobalProperties.service.version,
                name = GlobalProperties.service.name
            ),
            details = listOf(
                ApiIncidentResponse.Incident.Details(
                    code = getFullErrorCode(incident.code),
                    description = incident.description,
                    metadata = null
                )
            )
        )
    )

fun getFullErrorCode(code: String): String = "${code}/${GlobalProperties.service.id}"

val NaN: UUID
    get() = UUID(0, 0)

fun JsonNode.tryGetVersion(): Result<ApiVersion, DataErrors> {
    val name = "version"
    return tryGetTextAttribute(name).bind {
        when (val result = ApiVersion.tryValueOf(it)) {
            is Result.Success -> result
            is Result.Failure -> Result.failure(
                DataErrors.Validation.DataFormatMismatch(
                    name = name,
                    expectedFormat = "00.00.00",
                    actualValue = it
                )
            )
        }
    }
}

fun JsonNode.tryGetAction(): Result<CommandType, DataErrors> =
    tryGetAttributeAsEnum("action", CommandType)

fun <T : Any> JsonNode.tryGetParams(target: Class<T>): Result<T, Fail.Error> {
    val name = "params"
    return tryGetAttribute(name).bind {
        when (val result = it.tryToObject(target)) {
            is Result.Success -> result
            is Result.Failure -> Result.failure(
                BadRequest("Error parsing '$name'")
            )
        }
    }
}

fun JsonNode.tryGetId(): Result<UUID, DataErrors> {
    val name = "id"
    return tryGetTextAttribute(name)
        .bind {
            when (val result = it.tryUUID()) {
                is Result.Success -> result
                is Result.Failure -> Result.failure(
                    DataErrors.Validation.DataFormatMismatch(
                        name = name,
                        actualValue = it,
                        expectedFormat = "uuid"
                    )
                )
            }
        }
}

fun String.tryGetNode(): Result<JsonNode, BadRequest> =
    when (val result = this.tryToNode()) {
        is Result.Success -> result
        is Result.Failure -> Result.failure(BadRequest())
    }



