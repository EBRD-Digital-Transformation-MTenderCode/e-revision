package com.procurement.revision.application.model.amendment

import com.procurement.revision.domain.model.LotId
import com.procurement.revision.domain.model.Owner
import com.procurement.revision.domain.model.Token

class CheckExistingAmendmentForCancelLotContext(
    val cpid: String,
    val id: LotId,
    val token: Token,
    val owner: Owner
)
