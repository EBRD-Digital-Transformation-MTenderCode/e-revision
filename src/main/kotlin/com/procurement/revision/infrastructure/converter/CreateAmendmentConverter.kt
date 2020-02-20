package com.procurement.revision.infrastructure.converter

import com.procurement.revision.application.exception.ErrorException
import com.procurement.revision.application.exception.ErrorType
import com.procurement.revision.application.model.amendment.CreateAmendmentParams
import com.procurement.revision.application.model.amendment.CreateAmendmentResult
import com.procurement.revision.domain.enums.DocumentType
import com.procurement.revision.domain.model.amendment.Amendment
import com.procurement.revision.infrastructure.model.OperationType
import com.procurement.revision.infrastructure.web.dto.request.amendment.CreateAmendmentRequest

import com.procurement.revision.lib.errorIfEmpty

fun CreateAmendmentRequest.convert() = CreateAmendmentParams(
    id = id,
    operationType = OperationType.fromString(operationType),
    ocid = ocid,
    cpid = cpid,
    startDate = startDate,
    owner = owner,
    amendment = CreateAmendmentParams.Amendment(
        rationale = amendment.rationale,
        id = amendment.id,
        description = amendment.description,
        documents = amendment.documents
            .errorIfEmpty {
                ErrorException(
                    error = ErrorType.IS_EMPTY,
                    message = "The amendment with id '${amendment.id}' contains empty list of documents."
                )
            }
            ?.map { document ->
                CreateAmendmentParams.Amendment.Document(
                    id = document.id,
                    description = document.description,
                    documentType = DocumentType.fromString(document.documentType),
                    title = document.title
                )
            }.orEmpty()
    )
)

fun Amendment.convertToCreateAmendmentResult() = CreateAmendmentResult(
    amendment = CreateAmendmentResult.Amendment(
        rationale = rationale,
        description = description,
        relatedItem = relatedItem,
        relatesTo = relatesTo,
        status = status,
        id = id,
        type = type,
        date = date,
        token = token,
        documents = documents.map { document ->
            CreateAmendmentResult.Amendment.Document(
                id = document.id,
                description = document.description,
                documentType = document.documentType,
                title = document.title
            )
        }
    )
)