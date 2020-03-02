package com.procurement.revision.domain.util.extension

import com.procurement.revision.domain.functional.Result
import com.procurement.revision.infrastructure.bind.databinding.JsonDateTimeFormatter
import com.procurement.revision.infrastructure.fail.error.RequestError
import java.time.LocalDateTime
import java.util.*

fun String.tryCreateLocalDateTime() =
    try {
        Result.success(LocalDateTime.parse(this, JsonDateTimeFormatter.formatter))
    } catch (ex: Exception) {
        Result.failure(RequestError.ParsingError("Could not convert '$this' to date"))
    }

fun String.tryUUID(ofEntity: String): Result<UUID, RequestError.ParsingError> =
    try {
        Result.success(UUID.fromString(this))
    } catch (ex: Exception) {
        Result.failure(
            RequestError.ParsingError("Could not parse $ofEntity id ${this} to UUID type. ${ex.message}")
        )
    }