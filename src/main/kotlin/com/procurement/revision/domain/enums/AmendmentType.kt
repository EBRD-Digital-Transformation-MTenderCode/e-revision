package com.procurement.revision.domain.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class AmendmentType(@JsonValue override val key: String) : EnumElementProvider.Key {

    CANCELLATION("cancellation"),
    TENDER_CHANGE("tenderChange");

    override fun toString(): String = key

    companion object : EnumElementProvider<AmendmentType>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = AmendmentType.orThrow(name)
    }
}