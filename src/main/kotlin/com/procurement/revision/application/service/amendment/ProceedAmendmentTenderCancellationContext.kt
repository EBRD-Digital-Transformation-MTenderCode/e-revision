package com.procurement.revision.application.service.amendment

import com.procurement.revision.domain.model.Owner
import com.procurement.revision.domain.model.TenderId
import com.procurement.revision.domain.model.Token
import java.time.LocalDateTime

class ProceedAmendmentTenderCancellationContext(
    val cpid: String,
    val token: Token,
    val owner: Owner,
    val id: TenderId,
    val startDate: LocalDateTime
)
