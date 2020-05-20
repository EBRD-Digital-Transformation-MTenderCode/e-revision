package com.procurement.revision.domain.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class MainProcurementCategory(@JsonValue override val key: String) : EnumElementProvider.Key {
    GOODS("goods"),
    WORKS("works"),
    SERVICES("services");

    override fun toString(): String = key

    companion object : EnumElementProvider<MainProcurementCategory>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = MainProcurementCategory.orThrow(name)
    }
}