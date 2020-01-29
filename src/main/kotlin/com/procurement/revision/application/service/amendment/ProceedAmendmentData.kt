package com.procurement.revision.application.service.amendment

import com.procurement.revision.domain.enums.DocumentType

data class ProceedAmendmentData(
    val amendment: Amendment
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
