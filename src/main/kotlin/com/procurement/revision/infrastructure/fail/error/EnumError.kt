package com.procurement.revision.infrastructure.fail.error

import com.procurement.revision.infrastructure.fail.Fail

class EnumError(enumType: String, value: String, values: String) : Fail.Error("VR-") {
    override val code: String = "${prefix}1"
    override val description = "Unknown value for enumType $enumType: $value, Allowed values are $values"
}