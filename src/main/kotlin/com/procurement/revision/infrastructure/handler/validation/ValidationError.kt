package com.procurement.revision.infrastructure.handler.validation

import com.procurement.revision.domain.model.document.DocumentId

sealed class ValidationError(val code: String, val description: String) {

    class InvalidDocumentType(documentId: DocumentId) : ValidationError(
        code = "10.77",
        description = "Document '${documentId}' has invalid documentType."
    )

    class ParamsParsingError(message: String?) : ValidationError(
        code = "10.84",
        description = "Invalid params. $message"
    )
}

