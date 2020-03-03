package com.procurement.revision.domain.enums

import com.procurement.revision.domain.exception.EnumException
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.infrastructure.bind.databinding.Enumable
import com.procurement.revision.infrastructure.bind.databinding.Valuable
import com.procurement.revision.infrastructure.fail.error.EnumError

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

        fun tryFromString(value: String): Result<AmendmentType, EnumError> =
            elements[value.toUpperCase()]
                ?.let { Result.success(it) }
                ?: Result.failure(
                    EnumError(
                        enumType = AmendmentType::class.java.canonicalName,
                        value = value,
                        values = values().joinToString { it.text }
                    )
                )
    }
}
