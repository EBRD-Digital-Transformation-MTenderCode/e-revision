package com.procurement.revision.application.model.amendment

import com.procurement.revision.domain.enums.DocumentType
import com.procurement.revision.domain.functional.Option
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.model.Owner
import com.procurement.revision.domain.model.amendment.AmendmentId
import com.procurement.revision.domain.model.amendment.tryAmendmentId
import com.procurement.revision.domain.model.document.DocumentId
import com.procurement.revision.domain.model.document.tryDocumentId
import com.procurement.revision.domain.model.tryOwner
import com.procurement.revision.domain.util.extension.tryCreateLocalDateTime
import com.procurement.revision.infrastructure.fail.error.DataErrors
import com.procurement.revision.infrastructure.model.OperationType
import java.time.LocalDateTime

data class CreateAmendmentParams private constructor(
    val amendment: Amendment,
    val id: String,
    val operationType: OperationType,
    val startDate: LocalDateTime,
    val cpid: String,
    val ocid: String,
    val owner: Owner
) {
    companion object {
        fun tryCreate(
            amendment: Amendment,
            id: String,
            operationType: String,
            startDate: String,
            cpid: String,
            ocid: String,
            owner: String
        ): Result<CreateAmendmentParams, DataErrors> {

            val operationTypeResult = OperationType.tryFromString(operationType)
            if (operationTypeResult.isFail) return Result.failure(DataErrors.UnknownValue("operationType"))

            val startDateResult = startDate.tryCreateLocalDateTime()
            if (startDateResult.isFail) return Result.failure(DataErrors.DataTypeMismatch("startDate"))

            val ownerResult = owner.tryOwner()
            if (ownerResult.isFail) return Result.failure(DataErrors.DataTypeMismatch("owner"))

            return Result.success(
                CreateAmendmentParams(
                    cpid = cpid,
                    ocid = ocid,
                    operationType = operationTypeResult.get,
                    id = id,
                    owner = ownerResult.get,
                    amendment = amendment,
                    startDate = startDateResult.get
                )
            )
        }
    }

    data class Amendment private constructor(
        val rationale: String,
        val description: String?,
        val documents: List<Document>,
        val id: AmendmentId
    ) {

        companion object {
            fun tryCreate(
                id: String,
                rationale: String,
                description: String?,
                documents: Option<List<Document>>
            ): Result<Amendment, DataErrors> {
                if (documents.isDefined && documents.get.isEmpty())
                    return Result.failure(DataErrors.EmptyArray("amendment.documents"))

                val idResult = id.tryAmendmentId()
                if (idResult is Result.Failure) return Result.failure(DataErrors.DataTypeMismatch("amendment.id"))

                return Result.success(
                    Amendment(
                        id = idResult.get,
                        rationale = rationale,
                        description = description,
                        documents = if (documents.isDefined) documents.get else emptyList()
                    )
                )
            }
        }

        data class Document private constructor(
            val documentType: DocumentType,
            val id: DocumentId,
            val title: String,
            val description: String?
        ) {
            companion object {
                fun tryCreate(
                    documentType: String,
                    id: String,
                    title: String,
                    description: String?
                ): Result<Document, DataErrors> {
                    val idResult = id.tryDocumentId()
                    if (idResult.isFail) return Result.failure(DataErrors.DataTypeMismatch("document.id"))

                    val documentTypeResult = DocumentType.tryFromString(documentType)
                    if (documentTypeResult.isFail) return Result.failure(DataErrors.UnknownValue("documentType"))

                    return Result.success(
                        Document(
                            id = idResult.get,
                            description = description,
                            documentType = documentTypeResult.get,
                            title = title
                        )
                    )
                }
            }

            override fun equals(other: Any?): Boolean = if (this === other)
                true
            else
                other is DataValidationParams.Amendment.Document
                    && this.id == other.id

            override fun hashCode(): Int = id.hashCode()
        }
    }
}