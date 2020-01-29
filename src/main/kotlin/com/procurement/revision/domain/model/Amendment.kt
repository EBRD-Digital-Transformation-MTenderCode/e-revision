package com.procurement.revision.domain.model

import com.procurement.revision.domain.enums.AmendmentRelatesTo
import com.procurement.revision.domain.enums.AmendmentStatus
import com.procurement.revision.domain.enums.AmendmentType
import com.procurement.revision.domain.enums.DocumentType
import com.procurement.revision.domain.model.document.DocumentId
import java.time.LocalDateTime

data class Amendment(
    val id: AmendmentId,
    val token: Token,
    val owner: Owner,
    val date: LocalDateTime,
    val rationale: String,
    val description: String?,
    val status: AmendmentStatus,
    val type: AmendmentType,
    val relatesTo: AmendmentRelatesTo,
    val relatedItem: String,
    val documents: List<Document>
) {
    data class Document(
        val documentType: DocumentType,
        val id: DocumentId,
        val title: String,
        val description: String?
    )
}
