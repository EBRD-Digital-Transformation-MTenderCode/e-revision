package com.procurement.revision.infrastructure.fail.error

import com.procurement.revision.domain.model.document.DocumentId
import com.procurement.revision.infrastructure.fail.Fail

sealed class ValidationError(numberError: String, override val description: String) : Fail.Error("VR-") {
    override val code: String = prefix + numberError

    class InvalidDocumentType(documentId: DocumentId) : ValidationError(
        numberError = "1",
        description = "Document '${documentId}' has invalid documentType."
    )
}