package com.procurement.revision.infrastructure.model

import com.procurement.revision.domain.exception.EnumException
import com.procurement.revision.infrastructure.bind.databinding.Enumable
import com.procurement.revision.infrastructure.bind.databinding.Valuable

enum class OperationType(override val text: String) : Valuable<OperationType> {
    TENDER_CANCELLATION("tenderCancellation"),
    LOT_CANCELLATION("lotCancellation");

    override fun toString(): String = text

    companion object : Enumable<OperationType> {
        private val elements: Map<String, OperationType> = values().associateBy { it.text.toUpperCase() }

        override fun fromString(value: String): OperationType = elements[value.toUpperCase()]
            ?: throw EnumException(
                enumType = OperationType::class.java.canonicalName,
                value = value,
                values = values().joinToString { it.text }
            )
    }
}
