package com.procurement.revision.infrastructure.model

import com.procurement.revision.domain.exception.EnumException
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.infrastructure.bind.databinding.Enumable
import com.procurement.revision.infrastructure.bind.databinding.Valuable
import com.procurement.revision.infrastructure.fail.error.RequestError

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

        fun tryFromString(value: String): Result<OperationType, RequestError.EnumError> =
            elements[value.toUpperCase()]
                ?.let { Result.success(it) }
                ?: Result.failure(
                    RequestError.EnumError(
                        enumType = OperationType::class.java.canonicalName,
                        value = value,
                        values = values().joinToString { it.text }
                    )
                )
    }
}
