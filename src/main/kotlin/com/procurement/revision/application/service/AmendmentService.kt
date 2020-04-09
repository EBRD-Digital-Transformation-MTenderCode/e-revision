package com.procurement.revision.application.service

import com.procurement.revision.application.model.amendment.CheckAccessToAmendmentParams
import com.procurement.revision.application.model.amendment.CreateAmendmentParams
import com.procurement.revision.application.model.amendment.CreateAmendmentResult
import com.procurement.revision.application.model.amendment.DataValidationParams
import com.procurement.revision.application.model.amendment.GetAmendmentIdsParams
import com.procurement.revision.application.model.amendment.part.GetMainPartOfAmendmentParams
import com.procurement.revision.application.model.amendment.part.GetMainPartOfAmendmentResult
import com.procurement.revision.application.model.amendment.state.SetStateForAmendmentParams
import com.procurement.revision.application.model.amendment.state.SetStateForAmendmentResult
import com.procurement.revision.application.repository.AmendmentRepository
import com.procurement.revision.domain.enums.AmendmentRelatesTo
import com.procurement.revision.domain.enums.AmendmentStatus
import com.procurement.revision.domain.enums.AmendmentType
import com.procurement.revision.domain.enums.DocumentType
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.functional.Result.Companion.failure
import com.procurement.revision.domain.functional.Result.Companion.success
import com.procurement.revision.domain.functional.ValidationResult
import com.procurement.revision.domain.functional.asSuccess
import com.procurement.revision.domain.functional.bind
import com.procurement.revision.domain.model.amendment.Amendment
import com.procurement.revision.domain.model.amendment.AmendmentId
import com.procurement.revision.domain.util.extension.ifFalse
import com.procurement.revision.infrastructure.converter.convertToCreateAmendmentResult
import com.procurement.revision.infrastructure.converter.convertToGetMainPartOfAmendmentResult
import com.procurement.revision.infrastructure.converter.convertToSetStateForAmendmentResult
import com.procurement.revision.infrastructure.fail.Fail
import com.procurement.revision.infrastructure.fail.Fail.Incident.DatabaseConsistencyIncident
import com.procurement.revision.infrastructure.fail.error.ValidationError
import com.procurement.revision.infrastructure.model.OperationType
import com.procurement.revision.lib.toSetBy
import org.springframework.stereotype.Service

@Service
class AmendmentService(
    private val amendmentRepository: AmendmentRepository,
    private val generable: Generable
) {

    fun getAmendmentIdsBy(params: GetAmendmentIdsParams): Result<List<AmendmentId>, Fail.Incident> {
        val amendments = amendmentRepository.findBy(params.cpid, params.ocid)
            .doOnError { incident -> return failure(incident) }
            .get
        val relatedItems = params.relatedItems.toSet()

        return success(amendments.asSequence()
                           .filter { amendment ->
                               testEquals(amendment.status, pattern = params.status)
                                   && testEquals(amendment.type, pattern = params.type)
                                   && testEquals(amendment.relatesTo, pattern = params.relatesTo)
                                   && testContains(amendment.relatedItem, patterns = relatedItems)
                           }
                           .map { amendment -> amendment.id }
                           .toList())
    }

    fun validateDocumentsTypes(params: DataValidationParams): ValidationResult<Fail.Error> {
        val correctDocumentType = when (params.operationType) {
            OperationType.LOT_CANCELLATION, OperationType.TENDER_CANCELLATION -> DocumentType.CANCELLATION_DETAILS
        }
        params.amendment.documents
            .firstOrNull { document ->
                document.documentType != correctDocumentType
            }?.let { document ->
                return ValidationResult.error(ValidationError.InvalidDocumentType(document.id))
            }
        return ValidationResult.ok()
    }

    fun createAmendment(params: CreateAmendmentParams): Result<CreateAmendmentResult, Fail.Incident> {
        val relatesTo = when (params.operationType) {
            OperationType.TENDER_CANCELLATION -> AmendmentRelatesTo.TENDER
            OperationType.LOT_CANCELLATION -> AmendmentRelatesTo.LOT

        }

        val type = when (params.operationType) {
            OperationType.TENDER_CANCELLATION,
            OperationType.LOT_CANCELLATION -> {
                AmendmentType.CANCELLATION
            }
        }
        val createdAmendment = params.amendment
            .let { amendment ->
                Amendment(
                    id = amendment.id,
                    description = amendment.description,
                    rationale = amendment.rationale,
                    status = AmendmentStatus.PENDING,
                    type = type,
                    relatesTo = relatesTo,
                    relatedItem = params.relatedEntityId,
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
        return amendmentRepository.saveNewAmendment(
            cpid = params.cpid,
            ocid = params.ocid,
            amendment = createdAmendment
        ).bind { isSaved ->
            if (isSaved) {
                success(createdAmendment.convertToCreateAmendmentResult())
            } else {
                amendmentRepository.findBy(
                    cpid = params.cpid,
                    ocid = params.ocid,
                    id = createdAmendment.id
                ).bind { amendment ->
                    if (amendment != null)
                        success(amendment.convertToCreateAmendmentResult())
                    else
                        failure(DatabaseConsistencyIncident("Could not find ${createdAmendment.id}"))
                }
            }
        }
    }

    fun checkAccessToAmendment(params: CheckAccessToAmendmentParams): ValidationResult<Fail> {
        val amendment = amendmentRepository.findBy(
            cpid = params.cpid,
            ocid = params.ocid,
            id = params.amendmentId
        )
            .doReturn { incident -> return ValidationResult.error(incident) }
            ?: return ValidationResult.error(ValidationError.AmendmentNotFound(params.amendmentId))

        if (params.owner != amendment.owner)
            return ValidationResult.error(ValidationError.InvalidOwner())

        if (params.token != amendment.token)
            return ValidationResult.error(ValidationError.InvalidToken())

        return ValidationResult.ok()
    }

    fun getMainPartOfAmendment(params: GetMainPartOfAmendmentParams)
        : Result<List<GetMainPartOfAmendmentResult>, Fail> {
        val amendments = amendmentRepository.findBy(
            cpid = params.cpid,
            ocid = params.ocid,
            ids = params.amendmentIds
        ).orForwardFail { incident -> return incident }

        val amendmentIds = params.amendmentIds.toSetBy { it }
        val resultAmendmentIds = amendments.toSetBy { it.id }

        val absentAmendments = amendmentIds - resultAmendmentIds
        if (absentAmendments.isNotEmpty())
            return failure(
                ValidationError.AmendmentNotFoundOnGetMainPart(
                    absentAmendments.first()
                )
            )
        return amendments.map { amendment ->
            amendment.convertToGetMainPartOfAmendmentResult()
        }.asSuccess()
    }

    private fun <T> testEquals(value: T, pattern: T?): Boolean = if (pattern != null) value == pattern else true
    private fun <T> testContains(value: T, patterns: Set<T>): Boolean =
        if (patterns.isNotEmpty()) value in patterns else true

    fun setStateForAmendment(params: SetStateForAmendmentParams)
        : Result<SetStateForAmendmentResult, Fail> {
        val amendment = amendmentRepository.findBy(
            cpid = params.cpid,
            ocid = params.ocid,
            id = params.amendment.id
        ).orForwardFail { incident -> return incident }
            ?: return failure(
                ValidationError.AmendmentNotFoundOnSetStateForAmendment(
                    params.amendment.id
                )
            )
        val updatedAmendment = amendment.copy(
            status = params.amendment.status
        )

        amendmentRepository.updateAmendment(
            cpid = params.cpid, ocid = params.ocid, amendment = updatedAmendment
        )
            .orForwardFail { incident -> return incident }
            .ifFalse { return failure(DatabaseConsistencyIncident("Could not find amendment ${amendment.id}")) }

        return updatedAmendment
            .convertToSetStateForAmendmentResult()
            .asSuccess()
    }
}
