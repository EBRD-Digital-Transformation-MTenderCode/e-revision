package com.procurement.revision.infrastructure.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.revision.domain.enums.EnumElementProvider

enum class OperationType(@JsonValue override val key: String) : EnumElementProvider.Key {
    TENDER_CANCELLATION("tenderCancellation"),
    LOT_CANCELLATION("lotCancellation");

    override fun toString(): String = key

    companion object : EnumElementProvider<OperationType>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = OperationType.orThrow(name)
    }
}