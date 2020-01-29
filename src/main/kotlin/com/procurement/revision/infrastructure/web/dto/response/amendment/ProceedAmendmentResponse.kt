package com.procurement.revision.infrastructure.dto.amendment

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.revision.domain.enums.AmendmentRelatesTo
import com.procurement.revision.domain.enums.AmendmentStatus
import com.procurement.revision.domain.enums.AmendmentType
import com.procurement.revision.domain.enums.DocumentType
import com.procurement.revision.infrastructure.bind.databinding.JsonDateTimeDeserializer
import com.procurement.revision.infrastructure.bind.databinding.JsonDateTimeSerializer
import java.time.LocalDateTime
import java.util.*

data class ProceedAmendmentResponse(
    @field:JsonProperty("amendment") @param:JsonProperty("amendment") val amendment: Amendment
) {
    data class Amendment(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: UUID,

        @JsonDeserialize(using = JsonDateTimeDeserializer::class)
        @JsonSerialize(using = JsonDateTimeSerializer::class)
        @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,

        @field:JsonProperty("rationale") @param:JsonProperty("rationale") val rationale: String,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

        @field:JsonProperty("status") @param:JsonProperty("status") val status: AmendmentStatus,
        @field:JsonProperty("type") @param:JsonProperty("type") val type: AmendmentType,
        @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: AmendmentRelatesTo,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String?,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document>?
    ) {
        data class Document(
            @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: DocumentType,
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String?
        )
    }
}
