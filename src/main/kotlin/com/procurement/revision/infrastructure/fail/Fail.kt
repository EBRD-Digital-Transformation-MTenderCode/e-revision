package com.procurement.revision.infrastructure.fail

import com.procurement.revision.application.service.Logger
import com.procurement.revision.domain.enums.EnumElementProvider
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.functional.ValidationResult

sealed class Fail {

    abstract val code: String
    abstract val description: String
    val message: String
        get() = "ERROR CODE: '$code', DESCRIPTION: '$description'."

    abstract fun logging(logger: Logger)

    abstract class Error(val prefix: String) : Fail() {
        companion object {
            fun <T, E : Error> E.toResult(): Result<T, E> = Result.failure(this)
            fun <E : Error> E.toValidationResult(): ValidationResult<E> = ValidationResult.error(this)
        }

        override fun logging(logger: Logger) {
            logger.error(message = message)
        }
    }

    sealed class Incident(val level: Level, number: String, override val description: String) : Fail() {
        override val code: String = "INC-$number"

        override fun logging(logger: Logger) {
            when (level) {
                Level.ERROR -> logger.error(message)
                Level.WARNING -> logger.warn(message)
                Level.INFO -> logger.info(message)
            }
        }

        class DatabaseInteractionIncident(val exception: Exception) : Incident(
            level = Level.ERROR,
            number = "1",
            description = "Database incident."
        ) {
            override fun logging(logger: Logger) {
                logger.error(message = message, exception = exception)
            }
        }

        class DatabaseConsistencyIncident(message: String) : Incident(
            level = Level.ERROR,
            number = "2",
            description = "Database consistency incident. $message"
        )

        class ParseFromDatabaseIncident(val jsonData: String) : Incident(
            level = Level.ERROR,
            number = "3",
            description = "Could not parse data stored in database."
        ) {
            override fun logging(logger: Logger) {
                logger.error(message = message, mdc = mapOf("jsonData" to jsonData))
            }
        }

        class Parsing(className: String, val exception: Exception) : Incident(
            level = Level.ERROR,
            number = "4",
            description = "Error parsing to $className."
        )

        enum class Level(override val key: String) : EnumElementProvider.Key {
            ERROR("error"),
            WARNING("warning"),
            INFO("info");

            companion object : EnumElementProvider<Level>(info = info())
        }
    }
}




