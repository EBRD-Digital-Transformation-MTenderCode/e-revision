package com.procurement.revision.application.model.amendment

import com.procurement.revision.domain.enums.DocumentType
import com.procurement.revision.domain.model.Owner
import com.procurement.revision.domain.model.amendment.AmendmentId
import com.procurement.revision.domain.model.document.DocumentId
import com.procurement.revision.infrastructure.model.OperationType
import java.time.LocalDateTime
import java.util.*

data class CreateAmendmentParams(
    val amendment: Amendment,
    val id: UUID,
    val operationType: OperationType,
    val startDate: LocalDateTime,
    val cpid: String,
    val ocid: String,
    val owner: Owner
) {
    data class Amendment(
        val rationale: String,
        val description: String?,
        val documents: List<Document>,
        val id: AmendmentId
    ) {
        data class Document(
            val documentType: DocumentType,
            val id: DocumentId,
            val title: String,
            val description: String?
        )
    }
}
