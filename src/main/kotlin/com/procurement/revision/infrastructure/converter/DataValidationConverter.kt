package com.procurement.revision.infrastructure.converter

import com.procurement.revision.application.exception.ErrorException
import com.procurement.revision.application.exception.ErrorType
import com.procurement.revision.application.model.amendment.DataValidationParams
import com.procurement.revision.domain.enums.DocumentType
import com.procurement.revision.infrastructure.model.OperationType
import com.procurement.revision.infrastructure.web.dto.request.amendment.DataValidationRequest
import com.procurement.revision.lib.errorIfEmpty

fun DataValidationRequest.convert() = DataValidationParams(
    amendments = amendments.map { amendment ->
        DataValidationParams.Amendment(
            rationale = amendment.rationale,
            description = amendment.description,
            documents = amendment.documents.errorIfEmpty {
                ErrorException(
                    error = ErrorType.IS_EMPTY,
                    message = "The amendment with cpid '${cpid}' contains empty list of documents."
                )
            }?.map { document ->
                DataValidationParams.Amendment.Document(
                    id = document.id,
                    description = document.description,
                    documentType = DocumentType.fromString(document.documentType),
                    title = document.title
                )
            }.orEmpty()
        )
    },
    ocid = ocid,
    cpid = cpid,
    operationType = OperationType.fromString(operationType)
)

