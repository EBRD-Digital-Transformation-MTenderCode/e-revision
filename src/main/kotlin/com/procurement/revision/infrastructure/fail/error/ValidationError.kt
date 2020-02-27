package com.procurement.revision.infrastructure.fail.error

import com.procurement.revision.domain.model.document.DocumentId
import com.procurement.revision.infrastructure.fail.Fail

sealed class ValidationError(code: String, description: String) : Fail.Error(code, description) {

    class InvalidDocumentType(documentId: DocumentId) : ValidationError(
        code = "12.00",
        description = "Document '${documentId}' has invalid documentType."
    )

    class EmptyCollection(collection: String, wrapper: String) : ValidationError(
        code = "12.01",
        description = "$collection in $wrapper is empty."
    )
}