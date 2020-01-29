package com.procurement.revision.domain.enums

import com.procurement.revision.application.exception.EnumException
import com.procurement.revision.infrastructure.bind.databinding.Enumable
import com.procurement.revision.infrastructure.bind.databinding.Valuable

enum class AmendmentType(override val text: String) : Valuable<AmendmentType> {

    CANCELLATION("cancellation"),
    TENDER_CHANGE("tenderChange");

    override fun toString(): String = this.text

    companion object : Enumable<AmendmentType> {
        private val elements: Map<String, AmendmentType> = values().associateBy { it.text.toUpperCase() }

        override fun fromString(value: String): AmendmentType = elements[value.toUpperCase()]
            ?: throw EnumException(
                enumType = AmendmentType::class.java.canonicalName,
                value = value,
                values = values().joinToString { it.text }
            )
    }
}
