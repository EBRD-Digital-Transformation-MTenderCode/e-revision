package com.procurement.revision.infrastructure.web.dto.request.amendment


import com.fasterxml.jackson.annotation.JsonProperty

data class GetAmendmentByIdsRequest(
    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: String,
    @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: String,
    @param:JsonProperty("amendmentIds") @field:JsonProperty("amendmentIds") val amendmentIds: List<String>
)