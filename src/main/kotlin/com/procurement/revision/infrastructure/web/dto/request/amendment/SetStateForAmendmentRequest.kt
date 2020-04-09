package com.procurement.revision.infrastructure.web.dto.request.amendment


import com.fasterxml.jackson.annotation.JsonProperty

data class SetStateForAmendmentRequest(
    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: String,
    @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: String,
    @param:JsonProperty("amendment") @field:JsonProperty("amendment") val amendment: Amendment
) {
    data class Amendment(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
        @param:JsonProperty("status") @field:JsonProperty("status") val status: String
    )
}