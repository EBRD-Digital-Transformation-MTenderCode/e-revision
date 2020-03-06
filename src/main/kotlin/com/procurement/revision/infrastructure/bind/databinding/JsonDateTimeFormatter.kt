package com.procurement.revision.infrastructure.bind.databinding

import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

object JsonDateTimeFormatter {
    const val formatPattern = "uuuu-MM-dd'T'HH:mm:ss'Z'"
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(formatPattern)
        .withResolverStyle(ResolverStyle.STRICT)
}