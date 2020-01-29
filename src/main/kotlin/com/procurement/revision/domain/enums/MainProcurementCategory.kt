package com.procurement.revision.domain.enums

import com.procurement.revision.application.exception.EnumException
import com.procurement.revision.infrastructure.bind.databinding.Enumable
import com.procurement.revision.infrastructure.bind.databinding.Valuable

enum class MainProcurementCategory(override val text: String) : Valuable<MainProcurementCategory> {
    GOODS("goods"),
    WORKS("works"),
    SERVICES("services");

    companion object : Enumable<MainProcurementCategory> {
        private val elements: Map<String, MainProcurementCategory> = values().associateBy { it.text.toUpperCase() }

        override fun fromString(value: String): MainProcurementCategory = elements[value.toUpperCase()]
            ?: throw EnumException(
                enumType = MainProcurementCategory::class.java.canonicalName,
                value = value,
                values = values().joinToString { it.text }
            )
    }
}
