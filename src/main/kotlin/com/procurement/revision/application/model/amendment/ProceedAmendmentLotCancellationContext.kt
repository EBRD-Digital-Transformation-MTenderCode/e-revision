package com.procurement.revision.application.model.amendment

import com.procurement.revision.domain.model.lot.LotId
import com.procurement.revision.domain.model.Owner
import com.procurement.revision.domain.model.Token
import java.time.LocalDateTime

class ProceedAmendmentLotCancellationContext(
    val cpid: String,
    val token: Token,
    val owner: Owner,
    val id: LotId,
    val startDate: LocalDateTime
)
