package com.procurement.revision.application.model.amendment.part


import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.revision.domain.enums.AmendmentRelatesTo
import com.procurement.revision.domain.enums.AmendmentStatus
import com.procurement.revision.domain.enums.AmendmentType
import com.procurement.revision.domain.model.amendment.AmendmentId

data class GetMainPartOfAmendmentResult(
    @param:JsonProperty("id") @field:JsonProperty("id") val id: AmendmentId,
    @param:JsonProperty("status") @field:JsonProperty("status") val status: AmendmentStatus,
    @param:JsonProperty("type") @field:JsonProperty("type") val type: AmendmentType,
    @param:JsonProperty("relatesTo") @field:JsonProperty("relatesTo") val relatesTo: AmendmentRelatesTo,
    @param:JsonProperty("relateditem") @field:JsonProperty("relateditem") val relateditem: String
)