package com.procurement.revision.infrastructure.exception

import com.procurement.revision.domain.model.document.DocumentId

sealed class Fail {
    abstract val code: String
    abstract val description: String

    abstract class Error : Fail() {

        abstract class RequestError : Error() {
            class ParsingError(message: String) : RequestError() {
                override val code = "10.00"
                override val description = "Invalid JSON. '${message}'"
            }
        }

        abstract class ValidationError : Error() {
            class InvalidDocumentType(documentId: DocumentId) : ValidationError() {
                override val code = "10.10"
                override val description = "Document '${documentId}' has invalid documentType."
            }
        }

        class EntityNotFoundError(id: String) : Error() {
            override val code = "10.20"
            override val description = "Entity '$id' is not found."
        }
    }

    abstract class Incident : Fail() {
        class DatabaseIncident(message: String?) : Incident() {
            override val code = "20.00"
            override val description = "Database incident. $message"
        }
    }
}





