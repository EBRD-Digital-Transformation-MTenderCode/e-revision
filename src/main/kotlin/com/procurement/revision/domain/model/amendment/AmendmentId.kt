package com.procurement.revision.domain.model.amendment

import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.util.extension.tryUUID
import java.util.*

typealias AmendmentId = UUID

fun String.tryAmendmentId(): Result<AmendmentId, String> =
    this.tryUUID()
