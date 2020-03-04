package com.procurement.revision.application.repository

import com.procurement.revision.infrastructure.model.entity.HistoryEntity

interface HistoryRepository {
    fun getHistory(operationId: String, command: String): HistoryEntity?
    fun saveHistory(operationId: String, command: String, result: Any): HistoryEntity
}
