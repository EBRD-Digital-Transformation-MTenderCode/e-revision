package com.procurement.revision.infrastructure.fail.error

import com.procurement.revision.infrastructure.fail.Fail

sealed class DatabaseError(code: String, description: String) : Fail.Incident(code, description) {

    class EntityNotFoundError(id: String) : DatabaseError(
        code = "10.00",
        description = "Entity '$id' is not found."
    )
}