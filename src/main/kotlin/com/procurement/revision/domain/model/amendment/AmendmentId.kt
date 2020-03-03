package com.procurement.revision.domain.model.amendment

import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.util.extension.tryUUID
import com.procurement.revision.infrastructure.fail.error.ParsingError
import java.util.*

typealias AmendmentId = UUID

fun String.tryAmendmentId(): Result<AmendmentId, ParsingError> =
    this.tryUUID()
