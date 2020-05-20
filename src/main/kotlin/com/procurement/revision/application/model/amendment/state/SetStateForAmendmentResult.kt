package com.procurement.revision.application.model.amendment.state

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.revision.domain.enums.AmendmentStatus
import com.procurement.revision.domain.model.amendment.AmendmentId

data class SetStateForAmendmentResult(
    @param:JsonProperty("id") @field:JsonProperty("id") val id: AmendmentId,
    @param:JsonProperty("status") @field:JsonProperty("status") val status: AmendmentStatus
)