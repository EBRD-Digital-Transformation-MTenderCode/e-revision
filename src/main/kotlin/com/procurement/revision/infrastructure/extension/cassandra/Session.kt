package com.procurement.revision.infrastructure.extension.cassandra

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Session
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.functional.Result.Companion.failure
import com.procurement.revision.domain.functional.Result.Companion.success
import com.procurement.revision.infrastructure.fail.Fail

fun BoundStatement.tryExecute(session: Session): Result<ResultSet, Fail.Incident.DatabaseInteractionIncident> = try {
    success(session.execute(this))
} catch (expected: Exception) {
    failure(Fail.Incident.DatabaseInteractionIncident(exception = expected))
}