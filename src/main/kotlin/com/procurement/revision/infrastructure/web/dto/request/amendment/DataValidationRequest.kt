package com.procurement.revision.infrastructure.web.dto.request.amendment


import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

data class DataValidationRequest(
    @param:JsonProperty("amendment") @field:JsonProperty("amendment") val amendment: Amendment,
    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: String,
    @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: String,
    @param:JsonProperty("operationType") @field:JsonProperty("operationType") val operationType: String
) {
    data class Amendment(
        @param:JsonProperty("rationale") @field:JsonProperty("rationale") val rationale: String,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>?,
        @param:JsonProperty("id") @field:JsonProperty("id") val id: String
    ) {
        data class Document(
            @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: String,
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("title") @field:JsonProperty("title") val title: String,
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("description") @field:JsonProperty("description") val description: String?
        )
    }
}