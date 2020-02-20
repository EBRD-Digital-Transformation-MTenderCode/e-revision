package com.procurement.revision.application.service

import com.procurement.revision.application.exception.ErrorException
import com.procurement.revision.application.exception.ErrorType
import com.procurement.revision.application.model.amendment.CheckExistingAmendmentForCancelLotContext
import com.procurement.revision.application.model.amendment.CheckExistingAmendmentForCancelLotResult
import com.procurement.revision.application.model.amendment.CheckExistingAmendmentForCancelTenderContext
import com.procurement.revision.application.model.amendment.CheckExistingAmendmentForCancelTenderResult
import com.procurement.revision.application.model.amendment.CreateAmendmentParams
import com.procurement.revision.application.model.amendment.CreateAmendmentResult
import com.procurement.revision.application.model.amendment.DataValidationParams
import com.procurement.revision.application.model.amendment.GetAmendmentIdsParams
import com.procurement.revision.application.model.amendment.ProceedAmendmentData
import com.procurement.revision.application.model.amendment.ProceedAmendmentLotCancellationContext
import com.procurement.revision.application.model.amendment.ProceedAmendmentResult
import com.procurement.revision.application.model.amendment.ProceedAmendmentTenderCancellationContext
import com.procurement.revision.application.repository.AmendmentRepository
import com.procurement.revision.domain.enums.AmendmentRelatesTo
import com.procurement.revision.domain.enums.AmendmentStatus
import com.procurement.revision.domain.enums.AmendmentType
import com.procurement.revision.domain.enums.DocumentType
import com.procurement.revision.domain.model.Owner
import com.procurement.revision.domain.model.Token
import com.procurement.revision.domain.model.amendment.Amendment
import com.procurement.revision.domain.model.amendment.AmendmentId
import com.procurement.revision.infrastructure.converter.convertToCreateAmendmentResult
import com.procurement.revision.infrastructure.dto.converter.convert
import com.procurement.revision.infrastructure.model.OperationType
import com.procurement.revision.infrastructure.service.GenerationService
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AmendmentService(
    private val amendmentRepository: AmendmentRepository,
    private val generationService: GenerationService
) {

    fun proceedAmendmentForTenderCancellation(
        context: ProceedAmendmentTenderCancellationContext,
        data: ProceedAmendmentData
    ): ProceedAmendmentResult {

        val documentTypes = data.amendment.documents
            .associateBy(
                { it.id },
                { it.documentType }
            )
        checkDocumentsTypeForCancellation(documents = documentTypes)  // VR-3.17.1

        val createdAmendment = createAmendment(
            token = context.token,
            owner = context.owner,
            data = data,
            date = context.startDate,
            relatesTo = AmendmentRelatesTo.TENDER,
            relatedItem = context.id
        )

        amendmentRepository.saveNewAmendment(cpid = context.cpid, amendment = createdAmendment)
        return createdAmendment.convert()
    }

    fun proceedAmendmentForLotCancellation(
        context: ProceedAmendmentLotCancellationContext,
        data: ProceedAmendmentData
    ): ProceedAmendmentResult {
        val documentTypes = data.amendment.documents
            .associateBy(
                { it.id },
                { it.documentType }
            )
        checkDocumentsTypeForCancellation(documents = documentTypes)  // VR-3.17.1

        val createdAmendment = createAmendment(
            token = context.token,
            owner = context.owner,
            data = data,
            date = context.startDate,
            relatesTo = AmendmentRelatesTo.LOT,
            relatedItem = context.id.toString()
        )

        amendmentRepository.saveNewAmendment(cpid = context.cpid, amendment = createdAmendment)
        return createdAmendment.convert()
    }

    fun checkExistingAmendmentForCancelLot(
        context: CheckExistingAmendmentForCancelLotContext
    ): CheckExistingAmendmentForCancelLotResult {
        val amendmentsDB = amendmentRepository.findBy(context.cpid)
        val lotId = context.id.toString()
        amendmentsDB.asSequence()
            .filter { amendment ->
                amendment.type == AmendmentType.CANCELLATION && amendment.status == AmendmentStatus.PENDING
            }
            .forEach { amendment ->
                if (amendment.relatesTo == AmendmentRelatesTo.LOT && amendment.relatedItem == lotId)
                    throw ErrorException(
                        error = ErrorType.UNEXPECTED_AMENDMENT,
                        message = """ Found amendment assigned to lot for cancelling. Amendment id=${amendment.id}"""
                    )

                if (amendment.relatesTo == AmendmentRelatesTo.TENDER)
                    throw ErrorException(
                        error = ErrorType.UNEXPECTED_AMENDMENT,
                        message = """ Found amendment assigned to tender for cancelling. Amendment id=${amendment.id}"""
                    )
            }

        return CheckExistingAmendmentForCancelLotResult()
    }

    fun checkExistingAmendmentForCancelTender(
        context: CheckExistingAmendmentForCancelTenderContext
    ): CheckExistingAmendmentForCancelTenderResult {
        val amendmentsDB = amendmentRepository.findBy(context.cpid)
        amendmentsDB.asSequence()
            .filter { amendment ->
                amendment.type == AmendmentType.CANCELLATION && amendment.status == AmendmentStatus.PENDING
            }
            .forEach { amendment ->
                if (amendment.relatesTo == AmendmentRelatesTo.LOT)
                    throw ErrorException(
                        error = ErrorType.UNEXPECTED_AMENDMENT,
                        message = """ Found amendment assigned to lot relates to current tender for cancelling. 
                                     Amendment id=${amendment.id}"""
                    )

                if (amendment.relatesTo == AmendmentRelatesTo.TENDER)
                    throw ErrorException(
                        error = ErrorType.UNEXPECTED_AMENDMENT,
                        message = """ Found amendment assigned to tender for cancelling. Amendment id=${amendment.id}"""
                    )
            }

        return CheckExistingAmendmentForCancelTenderResult()
    }

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

    fun validateDocumentsTypes(params: DataValidationParams) {
        val correctDocumentType = when (params.operationType) {
            OperationType.LOT_CANCELLATION, OperationType.TENDER_CANCELLATION -> DocumentType.CANCELLATION_DETAILS
        }
        params.amendments
            .asSequence()
            .flatMap { amendment -> amendment.documents.asSequence() }
            .firstOrNull { document ->
                document.documentType != correctDocumentType
            }?.let { document ->
                throw ErrorException(
                    error = ErrorType.INVALID_DOCUMENT_TYPE,
                    message = "Document '${document.id}' has invalid documentType."
                )
            }
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
                    token = generationService.generateToken()
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

    private fun checkDocumentsTypeForCancellation(documents: Map<String, DocumentType>) {
        documents.forEach { (id, type) ->
            when (type) {
                DocumentType.CANCELLATION_DETAILS,
                DocumentType.CONFLICT_OF_INTEREST -> Unit

                DocumentType.EVALUATION_CRITERIA,
                DocumentType.ELIGIBILITY_CRITERIA,
                DocumentType.BILL_OF_QUANTITY,
                DocumentType.ILLUSTRATION,
                DocumentType.MARKET_STUDIES,
                DocumentType.TENDER_NOTICE,
                DocumentType.BIDDING_DOCUMENTS,
                DocumentType.PROCUREMENT_PLAN,
                DocumentType.TECHNICAL_SPECIFICATIONS,
                DocumentType.CONTRACT_DRAFT,
                DocumentType.HEARING_NOTICE,
                DocumentType.CLARIFICATIONS,
                DocumentType.ENVIRONMENTAL_IMPACT,
                DocumentType.ASSET_AND_LIABILITY_ASSESSMENT,
                DocumentType.RISK_PROVISIONS,
                DocumentType.COMPLAINTS,
                DocumentType.NEEDS_ASSESSMENT,
                DocumentType.FEASIBILITY_STUDY,
                DocumentType.PROJECT_PLAN,
                DocumentType.SHORTLISTED_FIRMS,
                DocumentType.EVALUATION_REPORTS,
                DocumentType.CONTRACT_ARRANGEMENTS,
                DocumentType.CONTRACT_GUARANTEES -> throw ErrorException(
                    error = ErrorType.INVALID_DOCUMENT_TYPE,
                    message = "Documents with id=${id} has not allowed type='${type}'."
                )
            }
        }
    }

    private fun createAmendment(
        data: ProceedAmendmentData,
        date: LocalDateTime,
        relatesTo: AmendmentRelatesTo,
        relatedItem: String,
        token: Token,
        owner: Owner
    ): Amendment {
        return Amendment(
            id = generationService.generateAmendmentId(),
            token = token,
            owner = owner,
            rationale = data.amendment.rationale,
            description = data.amendment.description,
            documents = data.amendment.documents
                .map { document ->
                    Amendment.Document(
                        id = document.id,
                        documentType = document.documentType,
                        description = document.description,
                        title = document.title
                    )
                },
            status = AmendmentStatus.PENDING,
            type = AmendmentType.CANCELLATION,
            date = date,
            relatesTo = relatesTo,
            relatedItem = relatedItem
        )
    }
}
