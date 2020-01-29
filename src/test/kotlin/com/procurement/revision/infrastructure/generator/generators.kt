package com.procurement.revision.infrastructure.generator

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.revision.infrastructure.web.dto.ApiVersion
import com.procurement.revision.infrastructure.web.dto.CommandMessage
import com.procurement.revision.infrastructure.web.dto.CommandType
import com.procurement.revision.infrastructure.web.dto.Context
import java.time.LocalDateTime
import java.util.*

object CommandMessageGenerator {
    const val COMMAND_ID = "COMMAND_ID"
    val COMMAND_VERSION = ApiVersion(major = 1, minor = 0, patch = 0)

    fun generate(
        id: String = COMMAND_ID,
        version: ApiVersion = COMMAND_VERSION,
        command: CommandType,
        context: Context,
        data: JsonNode
    ): CommandMessage {
        return CommandMessage(
            id = id,
            version = version,
            command = command,
            context = context,
            data = data
        )
    }
}

class ContextComposer private constructor(
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
    val token: UUID?,
    val startDate: LocalDateTime?,
    val endDate: LocalDateTime?,
    val id: String?

) {
    companion object {
        private const val CPID = "cpid-1"
        private const val OWNER = "owner-1"
        private const val COUNTRY = "MD"
        private const val STAGE = "stage"
        private const val PREV_STAGE = "prev_stage"

        private val TOKEN: UUID = UUID.fromString("bd56490f-57ca-4d1a-9210-250cb9b4eed3")
        private val START_DATE = LocalDateTime.parse("2011-06-05T17:59:00Z")
    }

    class Builder {

        private var operationId: String? = null
        private var requestId: String? = null
        private var cpid: String? = CPID
        private var ocid: String? = null
        private var stage: String? = STAGE
        private var prevStage: String? = PREV_STAGE
        private var processType: String? = null
        private var operationType: String? = null
        private var phase: String? = null
        private var owner: String? = OWNER
        private var country: String? = COUNTRY
        private var language: String? = null
        private var pmd: String? = null
        private var token: UUID? = TOKEN
        private var startDate: LocalDateTime? = START_DATE
        private var endDate: LocalDateTime? = null
        private var id: String? = null

        fun operationId(operationId: String) = apply { this.operationId = operationId }
        fun requestId(requestId: String) = apply { this.requestId = requestId }
        fun cpid(cpid: String) = apply { this.cpid = cpid }
        fun ocid(ocid: String) = apply { this.ocid = ocid }
        fun stage(stage: String) = apply { this.stage = stage }
        fun prevStage(prevStage: String) = apply { this.prevStage = prevStage }
        fun processType(processType: String) = apply { this.processType = processType }
        fun operationType(operationType: String) = apply { this.operationType = operationType }
        fun phase(phase: String) = apply { this.phase = phase }
        fun owner(owner: String) = apply { this.owner = owner }
        fun country(country: String) = apply { this.country = country }
        fun language(language: String) = apply { this.language = language }
        fun pmd(pmd: String) = apply { this.pmd = pmd }
        fun token(token: UUID) = apply { this.token = token }
        fun startDate(startDate: LocalDateTime) = apply { this.startDate = startDate }
        fun endDate(endDate: LocalDateTime) = apply { this.endDate = endDate }
        fun id(id: String) = apply { this.id = id }

        fun build() = ContextComposer(
            operationType = operationId,
            requestId = requestId,
            cpid = cpid,
            ocid = ocid,
            stage = stage,
            prevStage = prevStage,
            processType = processType,
            operationId = operationType,
            phase = phase,
            owner = owner,
            country = country,
            language = language,
            pmd = pmd,
            token = token,
            startDate = startDate,
            endDate = endDate,
            id = id
        )
    }
}
