package com.procurement.revision.domain.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class AmendmentRelatesTo(@JsonValue override val key: String) : EnumElementProvider.Key {
    LOT("lot"),
    TENDER("tender"),
    CAN("can");

    override fun toString(): String = key

    companion object : EnumElementProvider<AmendmentRelatesTo>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = AmendmentRelatesTo.orThrow(name)
    }
}