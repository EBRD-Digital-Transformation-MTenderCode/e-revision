package com.procurement.revision.infrastructure.exception

class DatabaseInteractionException : RuntimeException {
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(message: String) : super(message)
}
