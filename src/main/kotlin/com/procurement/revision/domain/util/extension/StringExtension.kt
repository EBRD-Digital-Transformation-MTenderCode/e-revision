package com.procurement.revision.domain.util.extension

import com.procurement.revision.domain.functional.Result
import com.procurement.revision.infrastructure.bind.databinding.JsonDateTimeFormatter
import java.time.LocalDateTime
import java.util.*

fun String.tryCreateLocalDateTime() =
    try {
        Result.success(LocalDateTime.parse(this, JsonDateTimeFormatter.formatter))
    } catch (ex: Exception) {
        Result.failure("Could not convert '$this' to date")
    }

fun String.tryUUID(): Result<UUID, String> =
    try {
        Result.success(UUID.fromString(this))
    } catch (ex: Exception) {
        Result.failure(
            "Could not parse $this to UUID type. ${ex.message}"
        )
    }