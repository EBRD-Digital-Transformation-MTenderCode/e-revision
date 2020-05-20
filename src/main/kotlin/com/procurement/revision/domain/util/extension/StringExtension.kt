package com.procurement.revision.domain.util.extension

import com.procurement.revision.domain.functional.Result
import com.procurement.revision.infrastructure.fail.Fail
import java.util.*

fun String.tryUUID(): Result<UUID, Fail.Incident.Parsing> =
    try {
        Result.success(UUID.fromString(this))
    } catch (ex: Exception) {
        Result.failure(
            Fail.Incident.Parsing(UUID::class.java.canonicalName, ex)
        )
    }