package com.procurement.revision.infrastructure.bind.databinding

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

interface Valuable<T : Enum<T>> {
    val text: String
}

class EnumSerializer<T> : JsonSerializer<T>() where T : Enum<T>, T : Valuable<T> {
    fun serialize(enum: T): String = enum.text

    override fun serialize(enum: T, jsonGenerator: JsonGenerator, provider: SerializerProvider) =
        jsonGenerator.writeString(serialize(enum))
}
