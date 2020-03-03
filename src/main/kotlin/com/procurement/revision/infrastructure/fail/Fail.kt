package com.procurement.revision.infrastructure.fail

import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.functional.ValidationResult

sealed class Fail {
    abstract class Error(val prefix: String) : Fail() {
        abstract val code: String
        abstract val description: String
        val message: String
            get() = "ERROR CODE: '$code', DESCRIPTION: '$description'."

        companion object {
            fun <T, E : Error> E.toResult(): Result<T, E> = Result.failure(this)
            fun <E : Error> E.toValidationResult(): ValidationResult<E> = ValidationResult.error(this)
        }
    }

    sealed class Incident : Fail() {
        abstract val code: String
        abstract val description: String

        class DatabaseInteractionIncident(message: String?) : Incident() {
            override val code = "20.00"
            override val description = "Database incident. $message"
        }
    }
}




