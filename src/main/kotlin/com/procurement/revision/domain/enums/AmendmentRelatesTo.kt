package com.procurement.revision.domain.enums

import com.procurement.revision.application.exception.EnumException
import com.procurement.revision.infrastructure.bind.databinding.Enumable
import com.procurement.revision.infrastructure.bind.databinding.Valuable

enum class AmendmentRelatesTo(override val text: String) : Valuable<AmendmentRelatesTo> {
    LOT("lot"),
    TENDER("tender"),
    CAN("can");

    override fun toString(): String = this.text

    companion object : Enumable<AmendmentRelatesTo> {
        private val elements: Map<String, AmendmentRelatesTo> = values().associateBy { it.text.toUpperCase() }

        override fun fromString(value: String): AmendmentRelatesTo = elements[value.toUpperCase()]
            ?: throw EnumException(
                enumType = AmendmentRelatesTo::class.java.canonicalName,
                value = value,
                values = values().joinToString { it.text }
            )
    }
}
