package com.procurement.revision.domain.model

import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.util.extension.tryUUID
import com.procurement.revision.infrastructure.fail.Fail
import java.util.*

typealias Owner = UUID

fun String.tryOwner(): Result<Owner, Fail.Incident.Parsing> =
    this.tryUUID()