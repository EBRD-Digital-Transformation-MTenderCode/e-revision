package com.procurement.revision.application.model.amendment

import com.procurement.revision.domain.enums.DocumentType
import com.procurement.revision.domain.functional.Option
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.functional.Result.Companion.failure
import com.procurement.revision.domain.functional.Result.Companion.success
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
        ): Result<CreateAmendmentParams, List<DataErrors>> {

            val operationTypeParsed = OperationType.tryFromString(operationType)
                .doOnError { return failure(listOf(DataErrors.UnknownValue("operationType"))) }
                .get

            val startDateParsed = startDate.tryCreateLocalDateTime()
                .doOnError { return failure(listOf(DataErrors.DataFormatMismatch("startDate"))) }
                .get

            val ownerParsed = owner.tryOwner()
                .doOnError { return failure(listOf(DataErrors.DataFormatMismatch("owner"))) }
                .get

            return success(
                CreateAmendmentParams(
                    cpid = cpid,
                    ocid = ocid,
                    operationType = operationTypeParsed,
                    id = id,
                    owner = ownerParsed,
                    amendment = amendment,
                    startDate = startDateParsed
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
            ): Result<Amendment, List<DataErrors>> {
                if (documents.isDefined && documents.get.isEmpty())
                    return failure(listOf(DataErrors.EmptyArray("amendment.documents")))

                val idParsed = id.tryAmendmentId()
                    .doOnError { return failure(listOf(DataErrors.DataFormatMismatch("amendment.id"))) }
                    .get

                return success(
                    Amendment(
                        id = idParsed,
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
                ): Result<Document, List<DataErrors>> {

                    val idParsed = id.tryDocumentId()
                        .doOnError { return failure(listOf(DataErrors.DataFormatMismatch("document.id"))) }
                        .get

                    val documentTypeParsed = DocumentType.tryFromString(documentType)
                        .doOnError { return failure(listOf(DataErrors.UnknownValue("documentType"))) }
                        .get

                    return success(
                        Document(
                            id = idParsed,
                            description = description,
                            documentType = documentTypeParsed,
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