package com.procurement.revision.infrastructure.fail.error

import com.procurement.revision.infrastructure.fail.Fail

sealed class RequestError(code: String, description: String) : Fail.Error(code, description) {

    class ParsingError(message: String) : RequestError(
        code = "11.00",
        description = "Invalid JSON. '${message}'"
    )

    class EnumError(enumType: String, value: String, values: String) : RequestError(
        code = "11.01",
        description = "Unknown value for enumType $enumType: $value, Allowed values are $values"
    )
}