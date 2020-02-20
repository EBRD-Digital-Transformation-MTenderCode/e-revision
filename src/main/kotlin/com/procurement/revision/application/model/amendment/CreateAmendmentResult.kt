package com.procurement.revision.application.model.amendment

import com.procurement.revision.domain.enums.AmendmentRelatesTo
import com.procurement.revision.domain.enums.AmendmentStatus
import com.procurement.revision.domain.enums.AmendmentType
import com.procurement.revision.domain.enums.DocumentType
import com.procurement.revision.domain.model.Token
import com.procurement.revision.domain.model.amendment.AmendmentId
import com.procurement.revision.domain.model.document.DocumentId
import java.time.LocalDateTime

data class CreateAmendmentResult(
    val amendment: Amendment
) {
    data class Amendment(
        val rationale: String,
        val description: String?,
        val documents: List<Document>,
        val id: AmendmentId,
        val date: LocalDateTime,
        val status: AmendmentStatus,
        val type: AmendmentType,
        val relatesTo: AmendmentRelatesTo,
        val relatedItem: String,
        val token: Token
    ) {
        data class Document(
            val documentType: DocumentType,
            val id: DocumentId,
            val title: String,
            val description: String?
        )
    }
}