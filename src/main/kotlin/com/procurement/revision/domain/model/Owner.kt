package com.procurement.revision.domain.model

import com.procurement.revision.domain.functional.Result
import com.procurement.revision.infrastructure.fail.Fail

typealias Owner = String

fun String.tryOwner(): Result<Owner, Fail.Incident.Parsing> =
    Result.success(this)