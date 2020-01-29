package com.procurement.revision.infrastructure.bind.databinding

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.time.LocalDateTime

class JsonDateTimeSerializer : JsonSerializer<LocalDateTime>() {
    companion object {
        fun serialize(date: LocalDateTime): String = date.format(JsonDateTimeFormatter.formatter)
    }

    override fun serialize(date: LocalDateTime, jsonGenerator: JsonGenerator, provider: SerializerProvider) =
        jsonGenerator.writeString(serialize(date))
}
