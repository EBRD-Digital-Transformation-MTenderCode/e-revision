package com.procurement.revision.infrastructure.bind.databinding

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

interface Enumable<T : Enum<T>> {
    fun fromString(value: String): T
}

class EnumDeserializer<T : Enum<T>>(val e: Enumable<T>) : JsonDeserializer<T>() {

    fun deserialize(value: String): T = e.fromString(value)

    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): T =
        deserialize(jsonParser.text)
}
