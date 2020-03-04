package com.procurement.revision.infrastructure.fail.error

import com.procurement.revision.infrastructure.fail.Fail

class ParsingError(message: String) : Fail.Error("PR-") {
    override val code: String = "${prefix}1"
    override val description = "Invalid JSON. '${message}'"
}