package com.procurement.revision.application.model.amendment.part


import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.revision.domain.enums.AmendmentRelatesTo
import com.procurement.revision.domain.enums.AmendmentStatus
import com.procurement.revision.domain.enums.AmendmentType
import com.procurement.revision.domain.enums.DocumentType
import com.procurement.revision.domain.model.amendment.AmendmentId
import com.procurement.revision.domain.model.document.DocumentId
import java.time.LocalDateTime

data class GetAmendmentByIdsResult(
    @param:JsonProperty("status") @field:JsonProperty("status") val status: AmendmentStatus,
    @param:JsonProperty("type") @field:JsonProperty("type") val type: AmendmentType,
    @param:JsonProperty("relatesTo") @field:JsonProperty("relatesTo") val relatesTo: AmendmentRelatesTo,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>,
    @param:JsonProperty("id") @field:JsonProperty("id") val id: AmendmentId,
    @param:JsonProperty("relatedItem") @field:JsonProperty("relatedItem") val relatedItem: String,
    @param:JsonProperty("rationale") @field:JsonProperty("rationale") val rationale: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,
    @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime
){
    data class Document(
        @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: DocumentType,
        @param:JsonProperty("id") @field:JsonProperty("id") val id: DocumentId,
        @param:JsonProperty("title") @field:JsonProperty("title") val title: String,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @param:JsonProperty("description") @field:JsonProperty("description") val description: String?
    )
}
