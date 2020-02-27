package com.procurement.revision.infrastructure.fail

sealed class Fail(val code: String, val description: String) {

    abstract class Error(code: String, description: String) : Fail(code, description)
    abstract class Incident(code: String, description: String) : Fail(code, description)
}





