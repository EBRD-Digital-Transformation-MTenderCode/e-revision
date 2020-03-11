package com.procurement.revision.application.repository

import com.procurement.revision.domain.functional.Result
import com.procurement.revision.infrastructure.fail.Fail
import com.procurement.revision.infrastructure.model.entity.HistoryEntity

interface HistoryRepository {
    fun getHistory(operationId: String, command: String): Result<HistoryEntity?, Fail.Incident>
    fun saveHistory(operationId: String, command: String, result: Any): Result<HistoryEntity, Fail.Incident>
}
