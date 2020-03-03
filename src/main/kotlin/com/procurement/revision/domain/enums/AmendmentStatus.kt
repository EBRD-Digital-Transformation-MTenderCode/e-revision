package com.procurement.revision.domain.enums

import com.procurement.revision.domain.exception.EnumException
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.infrastructure.bind.databinding.Enumable
import com.procurement.revision.infrastructure.bind.databinding.Valuable
import com.procurement.revision.infrastructure.fail.error.EnumError

enum class AmendmentStatus(override val text: String) : Valuable<AmendmentStatus> {

    PENDING("pending"),
    ACTIVE("active"),
    WITHDRAWN("withdrawn"),
    CANCELLED("cancelled");

    companion object : Enumable<AmendmentStatus> {
        private val elements: Map<String, AmendmentStatus> = values().associateBy { it.text.toUpperCase() }

        override fun fromString(value: String): AmendmentStatus = elements[value.toUpperCase()]
            ?: throw EnumException(
                enumType = AmendmentStatus::class.java.canonicalName,
                value = value,
                values = values().joinToString { it.text }
            )

        fun tryFromString(value: String): Result<AmendmentStatus, EnumError> =
            elements[value.toUpperCase()]
                ?.let { Result.success(it) }
                ?: Result.failure(
                    EnumError(
                        enumType = AmendmentStatus::class.java.canonicalName,
                        value = value,
                        values = values().joinToString { it.text }
                    )
                )
    }
}
