package com.procurement.revision.domain.model

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.revision.domain.enums.EnumElementProvider.Companion.keysAsStringsUpper
import com.procurement.revision.domain.enums.Stage

class Ocid private constructor(private val value: String) {

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is Ocid
                && this.value == other.value
        else
            true
    }

    override fun hashCode(): Int = value.hashCode()

    @JsonValue
    override fun toString(): String = value

    companion object {
        private val STAGES: String
            get() = Stage.allowedElements.keysAsStringsUpper().joinToString(separator = "|", prefix = "(", postfix = ")")

        private val regex = "^[a-z]{4}-[a-z0-9]{6}-[A-Z]{2}-[0-9]{13}-$STAGES-[0-9]{13}\$".toRegex()

        val pattern: String
            get() = regex.pattern

        fun tryCreateOrNull(value: String): Ocid? = if (value.matches(regex)) Ocid(value = value) else null
    }
}
