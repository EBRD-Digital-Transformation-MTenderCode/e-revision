package com.procurement.revision.infrastructure.fail.incident

import com.procurement.revision.infrastructure.fail.Fail

abstract class DatabaseIncident(code: String, description: String) : Fail.Incident(code, description) {

    class DatabaseInteractionIncident(message: String?) : Incident(
        code = "20.00",
        description = "Database incident. $message"
    )
}