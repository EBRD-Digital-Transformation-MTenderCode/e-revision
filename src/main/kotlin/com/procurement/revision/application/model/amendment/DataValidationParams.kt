package com.procurement.revision.application.model.amendment

import com.procurement.revision.domain.enums.DocumentType
import com.procurement.revision.infrastructure.model.OperationType

data class DataValidationParams(

    val amendments: List<Amendment>,
    val cpid: String,
    val ocid: String,
    val operationType: OperationType
) {
    data class Amendment(
        val rationale: String,
        val description: String?,
        val documents: List<Document>
    ) {
        data class Document(
            val documentType: DocumentType,
            val id: String,
            val title: String,
            val description: String?
        )
    }
}
