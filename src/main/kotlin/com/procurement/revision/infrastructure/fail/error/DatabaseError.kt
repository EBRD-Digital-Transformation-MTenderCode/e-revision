package com.procurement.revision.infrastructure.fail.error

import com.procurement.revision.infrastructure.fail.Fail

sealed class DatabaseError(numberError: String, override val description: String) : Fail.Error("DB-") {
    override val code: String = prefix + numberError

    class EntityNotFoundError(id: String) : DatabaseError(numberError = "1", description = "Entity '$id' is not found.")
}