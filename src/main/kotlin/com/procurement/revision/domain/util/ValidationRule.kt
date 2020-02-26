package com.procurement.revision.domain.util

typealias ValidationRule<T, E> = (T) -> ValidationResult<E>
