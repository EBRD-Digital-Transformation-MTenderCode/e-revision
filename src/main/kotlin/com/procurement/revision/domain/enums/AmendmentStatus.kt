package com.procurement.revision.domain.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class AmendmentStatus(@JsonValue override val key: String) : EnumElementProvider.Key {
    PENDING("pending"),
    ACTIVE("active"),
    WITHDRAWN("withdrawn"),
    CANCELLED("cancelled");

    override fun toString(): String = key

    companion object : EnumElementProvider<AmendmentStatus>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = AmendmentStatus.orThrow(name)
    }
}