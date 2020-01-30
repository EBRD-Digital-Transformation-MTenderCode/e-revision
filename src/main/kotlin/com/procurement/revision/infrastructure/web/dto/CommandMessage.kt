package com.procurement.revision.infrastructure.web.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.revision.domain.exception.EnumException
import com.procurement.revision.application.exception.ErrorException
import com.procurement.revision.application.exception.ErrorType
import com.procurement.revision.domain.model.LotId
import com.procurement.revision.domain.model.TenderId
import com.procurement.revision.infrastructure.bind.apiversion.ApiVersionDeserializer
import com.procurement.revision.infrastructure.bind.apiversion.ApiVersionSerializer
import com.procurement.revision.infrastructure.configuration.properties.GlobalProperties
import com.procurement.revision.infrastructure.utils.toLocal
import java.time.LocalDateTime
import java.util.*

data class CommandMessage @JsonCreator constructor(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
    @field:JsonProperty("command") @param:JsonProperty("command") val command: CommandType,
    @field:JsonProperty("context") @param:JsonProperty("context") val context: Context,
    @field:JsonProperty("data") @param:JsonProperty("data") val data: JsonNode,

    @JsonDeserialize(using = ApiVersionDeserializer::class)
    @JsonSerialize(using = ApiVersionSerializer::class)
    @field:JsonProperty("version") @param:JsonProperty("version") val version: ApiVersion
)

val CommandMessage.cpid: String
    get() = this.context.cpid
        ?: throw ErrorException(
            error = ErrorType.CONTEXT,
            message = "Missing the 'cpid' attribute in context."
        )

val CommandMessage.token: UUID
    get() = this.context.token
        ?.let { id ->
            try {
                UUID.fromString(id)
            } catch (ignore: Exception) {
                throw ErrorException(error = ErrorType.INVALID_FORMAT_TOKEN)
            }
        }
        ?: throw ErrorException(
            error = ErrorType.CONTEXT,
            message = "Missing the 'token' attribute in context."
        )

val CommandMessage.owner: String
    get() = this.context.owner
        ?: throw ErrorException(
            error = ErrorType.CONTEXT,
            message = "Missing the 'owner' attribute in context."
        )

val CommandMessage.lotId: LotId
    get() = this.context.id?.let { id ->
        try {
            LotId.fromString(id)
        } catch (expected: Exception) {
            throw ErrorException(error = ErrorType.INVALID_FORMAT_LOT_ID)
        }
    } ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'id' attribute in context.")

val CommandMessage.tenderId: TenderId
    get() = this.context.id
        ?: throw ErrorException(
            error = ErrorType.CONTEXT,
            message = "Missing the 'id' attribute in context."
        )

val CommandMessage.startDate: LocalDateTime
    get() = this.context.startDate?.toLocal()
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'startDate' attribute in context.")

data class Context @JsonCreator constructor(
    val operationId: String?,
    val requestId: String?,
    val cpid: String?,
    val ocid: String?,
    val stage: String?,
    val prevStage: String?,
    val processType: String?,
    val operationType: String?,
    val phase: String?,
    val owner: String?,
    val country: String?,
    val language: String?,
    val pmd: String?,
    val token: String?,
    val startDate: String?,
    val endDate: String?,
    val id: String?
)

enum class CommandType(private val value: String) {

    PROCEED_AMENDMENT_FOR_TENDER_CANCELLATION("proceedAmendmentForTenderCancellation"),
    PROCEED_AMENDMENT_FOR_LOT_CANCELLATION("proceedAmendmentForLotCancellation"),
    CHECK_EXISTING_AMENDMENT_FOR_CANCEL_LOT("checkExistingAmendmentForCancelLot"),
    CHECK_EXISTING_AMENDMENT_FOR_CANCEL_TENDER("checkExistingAmendmentForCancelTender");

    @JsonValue
    fun value(): String {
        return this.value
    }

    override fun toString(): String {
        return this.value
    }
}

fun errorResponse(exception: Exception, id: String, version: ApiVersion): ApiErrorResponse =
    when (exception) {
        is ErrorException                                                             -> getApiErrorResponse(
            id = id,
            version = version,
            code = exception.code,
            message = exception.message!!
        )
        is EnumException -> getApiErrorResponse(
            id = id,
            version = version,
            code = exception.code,
            message = exception.message!!
        )
        else                                                                          -> getApiErrorResponse(
            id = id,
            version = version,
            code = "00.00",
            message = exception.message!!
        )
    }

private fun getApiErrorResponse(id: String, version: ApiVersion, code: String, message: String): ApiErrorResponse {
    return ApiErrorResponse(
        errors = listOf(
            ApiErrorResponse.Error(
                code = "400.${GlobalProperties.serviceId}." + code,
                description = message
            )
        ),
        id = id,
        version = version
    )
}
