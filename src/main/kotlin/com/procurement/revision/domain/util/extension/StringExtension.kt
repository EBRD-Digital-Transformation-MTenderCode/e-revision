package com.procurement.revision.domain.util.extension

import com.procurement.revision.domain.functional.Result
import java.util.*

fun String.tryUUID(): Result<UUID, String> =
    try {
        Result.success(UUID.fromString(this))
    } catch (ex: Exception) {
        Result.failure(
            "Could not parse $this to UUID type. ${ex.message}"
        )
    }