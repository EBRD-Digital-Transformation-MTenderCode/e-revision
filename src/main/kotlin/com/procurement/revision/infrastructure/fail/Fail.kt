package com.procurement.revision.infrastructure.fail

import com.procurement.revision.domain.enums.EnumElementProvider
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

    sealed class Incident(val level: Level, number: String, val description: String) : Fail() {
        val code: String = "INC-$number"

        class DatabaseInteractionIncident(exception: Exception) : Incident(
            level = Level.ERROR,
            number = "1",
            description = "Database incident. ${exception.message}"
        )

        class DatabaseConsistencyIncident(message: String) : Incident(
            level = Level.ERROR,
            number = "2",
            description = "Database consistency incident. $message"
        )

        enum class Level(override val key: String) : EnumElementProvider.Key {
            ERROR("error"),
            WARNING("warning"),
            INFO("info");

            companion object : EnumElementProvider<Level>(info = info())
        }
    }
}




