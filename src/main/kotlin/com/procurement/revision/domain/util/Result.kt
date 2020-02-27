package com.procurement.revision.domain.util

sealed class Result<out T, out E> {
    companion object {
        fun <T> pure(value: T) = Success(value)
        fun <T> success(value: T) = Success(value)
        fun <T> failure(value: T) = Failure(value)
    }

    abstract val isSuccess: Boolean
    abstract val isFail: Boolean

    abstract val get: T
    abstract val error: E

    val orNull: T?
        get() = when (this) {
            is Success -> get
            is Failure -> null
        }

    val asOption: Option<T>
        get() = when (this) {
            is Success -> Option.pure(get)
            is Failure -> Option.none()
        }

    fun <R : Exception> orThrow(block: (E) -> R): T = when (this) {
        is Success -> get
        is Failure -> throw block(this.error)
    }

    fun <R> map(block: (T) -> R): Result<R, E> = this.flatMap {
        Success(
            block(it)
        )
    }

    fun <R> mapError(block: (E) -> R): Result<T, R> = this.flatMapError { Failure(block(it)) }

    data class Success<out T> internal constructor(private val value: T) : Result<T, Nothing>() {
        override val isSuccess: Boolean = true
        override val isFail: Boolean = false

        override val get: T = value
        override val error: Nothing
            get() = throw NoSuchElementException("The result does not contain a error.")
    }

    data class Failure<out E> internal constructor(private val value: E) : Result<Nothing, E>() {
        override val isSuccess: Boolean = true
        override val isFail: Boolean = false

        override val get: Nothing
            get() = throw NoSuchElementException("The result does not contain a value.")

        override val error: E = value
    }
}

fun <T, E> Result<T, E>.validate(block: ValidationRule<T, E>): Result<T, E> = when (this) {
    is Result.Success -> {
        val result = block(this.get)
        if (result.isError) Result.failure(result.get) else this
    }
    is Result.Failure -> this
}

fun <T, R, E> Result<T, E>.flatMap(block: (T) -> Result<R, E>): Result<R, E> = when (this) {
    is Result.Success -> block(this.get)
    is Result.Failure -> this
}

fun <T, E, R> Result<T, E>.flatMapError(block: (E) -> Result<T, R>): Result<T, R> = when (this) {
    is Result.Success -> this
    is Result.Failure -> block(this.error)
}
