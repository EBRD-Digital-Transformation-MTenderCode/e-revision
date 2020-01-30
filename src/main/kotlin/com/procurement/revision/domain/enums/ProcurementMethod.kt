package com.procurement.revision.domain.enums

import com.procurement.revision.domain.exception.EnumException
import com.procurement.revision.infrastructure.bind.databinding.Enumable
import com.procurement.revision.infrastructure.bind.databinding.Valuable

enum class ProcurementMethod(override val text: String) : Valuable<ProcurementMethod> {
    MV("open"),
    OT("open"),
    RT("selective"),
    SV("open"),
    DA("limited"),
    NP("limited"),
    FA("limited"),
    OP("selective"),
    TEST_OT("open"),
    TEST_SV("open"),
    TEST_RT("selective"),
    TEST_MV("open"),
    TEST_DA("limited"),
    TEST_NP("limited"),
    TEST_FA("limited"),
    TEST_OP("selective");

    companion object: Enumable<ProcurementMethod> {
        private val elements: Map<String, ProcurementMethod> = values().associateBy { it.text.toUpperCase() }

        fun <T : Exception> valueOrException(name: String, block: (Exception) -> T): ProcurementMethod = try {
            valueOf(name)
        } catch (expected: Exception) {
            throw block(expected)
        }

        override fun fromString(value: String): ProcurementMethod = elements[value.toUpperCase()]
            ?: throw EnumException(
                enumType = ProcurementMethod::class.java.canonicalName,
                value = value,
                values = values().joinToString { it.text }
            )
    }
}
