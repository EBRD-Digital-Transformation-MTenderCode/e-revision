package com.procurement.revision.infrastructure.web.dto.request.amendment

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.revision.domain.enums.AmendmentRelatesTo
import com.procurement.revision.domain.enums.AmendmentStatus
import com.procurement.revision.domain.enums.AmendmentType

data class GetAmendmentIdsRequest(
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @param:JsonProperty("status") @field:JsonProperty("status") val status: AmendmentStatus?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @param:JsonProperty("type") @field:JsonProperty("type") val type: AmendmentType?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @param:JsonProperty("relatesTo") @field:JsonProperty("relatesTo") val relatesTo: AmendmentRelatesTo?,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @param:JsonProperty("relatedItems") @field:JsonProperty("relatedItems") val relatedItems: List<String>?,

    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: String,
    @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: String
)