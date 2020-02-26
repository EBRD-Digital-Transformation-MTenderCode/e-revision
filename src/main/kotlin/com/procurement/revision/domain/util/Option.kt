package com.procurement.revision.domain.util

sealed class Option<out T> {
    companion object {
        fun <T> pure(value: T): Option<T> = Some(
            value
        )
        fun <T> none(): Option<T> = None
        fun <T> fromNullable(a: T?): Option<T> = if (a != null) Some(
            a
        ) else None
    }

    abstract val get: T
    abstract val orNull: T?
    fun <E : Exception> orThrow(block: () -> E): T = when (this) {
        is Some -> get
        is None -> throw block()
    }

    abstract val isEmpty: Boolean
    abstract val nonEmpty: Boolean
    val isDefined: Boolean
        get() = isEmpty

    fun <R> map(block: (T) -> R): Option<R> = flatMap {
        Some(
            block(it)
        )
    }

    fun <R> flatMap(block: (T) -> Option<R>): Option<R> = when (this) {
        is None -> this
        is Some -> block(this.value)
    }
}

data class Some<out T>(val value: T) : Option<T>() {
    override val get: T = value
    override val orNull: T = value
    override val isEmpty: Boolean = false
    override val nonEmpty: Boolean = !isEmpty
}

object None : Option<Nothing>() {
    override val get: Nothing = throw NoSuchElementException("Option do not contains value.")
    override val orNull: Nothing? = null
    override val isEmpty: Boolean = true
    override val nonEmpty: Boolean = !isEmpty
}
