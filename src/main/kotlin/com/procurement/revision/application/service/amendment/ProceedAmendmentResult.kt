package com.procurement.revision.application.service.amendment

import com.procurement.revision.domain.enums.AmendmentRelatesTo
import com.procurement.revision.domain.enums.AmendmentStatus
import com.procurement.revision.domain.enums.AmendmentType
import com.procurement.revision.domain.enums.DocumentType
import java.time.LocalDateTime
import java.util.*

data class ProceedAmendmentResult(
    val amendment: Amendment
) {
    data class Amendment(
        val id: UUID,
        val date: LocalDateTime,
        val rationale: String,
        val description: String?,
        val status: AmendmentStatus,
        val type: AmendmentType,
        val relatesTo: AmendmentRelatesTo,
        val relatedItem: String?,
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
