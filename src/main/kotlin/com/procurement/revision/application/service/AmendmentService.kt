package com.procurement.revision.application.service

import com.procurement.revision.application.model.amendment.CreateAmendmentParams
import com.procurement.revision.application.model.amendment.CreateAmendmentResult
import com.procurement.revision.application.model.amendment.DataValidationParams
import com.procurement.revision.application.model.amendment.GetAmendmentIdsParams
import com.procurement.revision.application.repository.AmendmentRepository
import com.procurement.revision.domain.ValidationResult
import com.procurement.revision.domain.enums.AmendmentRelatesTo
import com.procurement.revision.domain.enums.AmendmentStatus
import com.procurement.revision.domain.enums.AmendmentType
import com.procurement.revision.domain.enums.DocumentType
import com.procurement.revision.domain.model.amendment.Amendment
import com.procurement.revision.domain.model.amendment.AmendmentId
import com.procurement.revision.infrastructure.converter.convertToCreateAmendmentResult
import com.procurement.revision.infrastructure.handler.validation.ValidationError
import com.procurement.revision.infrastructure.model.OperationType
import org.springframework.stereotype.Service

@Service
class AmendmentService(
    private val amendmentRepository: AmendmentRepository,
    private val generable: Generable
) {

    fun getAmendmentIdsBy(params: GetAmendmentIdsParams): List<AmendmentId> {
        val amendments = amendmentRepository.findBy(params.cpid)
        val relatedItems = params.relatedItems.toSet()

        return amendments.asSequence()
            .filter { amendment ->
                testEquals(amendment.status, pattern = params.status)
                    && testEquals(amendment.type, pattern = params.type)
                    && testEquals(amendment.relatesTo, pattern = params.relatesTo)
                    && testContains(amendment.relatedItem, patterns = relatedItems)
            }
            .map { amendment -> amendment.id }
            .toList()
    }

    fun validateDocumentsTypes(params: DataValidationParams): ValidationResult<ValidationError> {
        val correctDocumentType = when (params.operationType) {
            OperationType.LOT_CANCELLATION, OperationType.TENDER_CANCELLATION -> DocumentType.CANCELLATION_DETAILS
        }
        params.amendments
            .asSequence()
            .flatMap { amendment -> amendment.documents.asSequence() }
            .firstOrNull { document ->
                document.documentType != correctDocumentType
            }?.let { document ->
                return ValidationResult.error(ValidationError.InvalidDocumentType(document.id))
            }
        return ValidationResult.ok()
    }

    fun createAmendment(params: CreateAmendmentParams): CreateAmendmentResult {
        val relatesTo = when (params.operationType) {
            OperationType.TENDER_CANCELLATION -> {
                AmendmentRelatesTo.TENDER
            }
            OperationType.LOT_CANCELLATION -> {
                AmendmentRelatesTo.LOT
            }
        }
        val createdAmendment = params.amendment
            .let { amendment ->
                Amendment(
                    id = amendment.id,
                    description = amendment.description,
                    rationale = amendment.rationale,
                    status = AmendmentStatus.PENDING,
                    type = AmendmentType.CANCELLATION,
                    relatesTo = relatesTo,
                    relatedItem = params.id.toString(),
                    date = params.startDate,
                    documents = amendment.documents.map { document ->
                        Amendment.Document(
                            id = document.id,
                            description = document.description,
                            title = document.title,
                            documentType = document.documentType
                        )
                    },
                    owner = params.owner,
                    token = generable.generateToken()
                )
            }
        val isSaved = amendmentRepository.saveNewAmendment(cpid = params.cpid, amendment = createdAmendment)
        return if (isSaved)
            createdAmendment.convertToCreateAmendmentResult()
        else {
            amendmentRepository.findBy(params.cpid, createdAmendment.id)!!.convertToCreateAmendmentResult()
        }
    }

    private fun <T> testEquals(value: T, pattern: T?): Boolean = if (pattern != null) value == pattern else true
    private fun <T> testContains(value: T, patterns: Set<T>): Boolean =
        if (patterns.isNotEmpty()) value in patterns else true
}
