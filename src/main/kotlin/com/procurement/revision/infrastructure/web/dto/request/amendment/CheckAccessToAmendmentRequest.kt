package com.procurement.revision.infrastructure.web.dto.request.amendment


import com.fasterxml.jackson.annotation.JsonProperty

data class CheckAccessToAmendmentRequest(
    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: String,
    @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: String,
    @param:JsonProperty("token") @field:JsonProperty("token") val token: String,
    @param:JsonProperty("owner") @field:JsonProperty("owner") val owner: String,
    @param:JsonProperty("amendmentId") @field:JsonProperty("amendmentId") val amendmentId: String
)