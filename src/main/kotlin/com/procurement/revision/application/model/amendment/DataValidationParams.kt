package com.procurement.revision.application.model.amendment

import com.procurement.revision.domain.enums.DocumentType
import com.procurement.revision.domain.functional.Option
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.functional.Result.Companion.failure
import com.procurement.revision.domain.functional.Result.Companion.success
import com.procurement.revision.domain.model.amendment.AmendmentId
import com.procurement.revision.domain.model.amendment.tryAmendmentId
import com.procurement.revision.domain.model.document.tryDocumentId
import com.procurement.revision.infrastructure.fail.error.DataErrors
import com.procurement.revision.infrastructure.model.OperationType

data class DataValidationParams private constructor(
    val amendments: List<Amendment>,
    val cpid: String,
    val ocid: String,
    val operationType: OperationType
) {
    companion object {
        fun tryCreate(
            amendments: List<Amendment>,
            cpid: String,
            ocid: String,
            operationType: String
        ): Result<DataValidationParams, List<DataErrors>> {
            if (amendments.isEmpty()) {
                return failure(listOf(DataErrors.EmptyArray("amendments")))
            }

            val operationTypeParsed = OperationType.tryFromString(operationType)
                .doOnError { return failure(listOf(DataErrors.UnknownValue("operationType"))) }
                .get

            return success(
                DataValidationParams(
                    cpid = cpid,
                    ocid = ocid,
                    operationType = operationTypeParsed,
                    amendments = amendments
                )
            )
        }
    }

    data class Amendment private constructor(
        val id: AmendmentId,
        val rationale: String,
        val description: String?,
        val documents: List<Document>
    ) {
        companion object {
            fun tryCreate(
                id: String,
                rationale: String,
                description: String?,
                documents: Option<List<Document>>
            ): Result<Amendment, List<DataErrors>> {
                if (documents.isDefined && documents.get.isEmpty())
                    return failure(listOf(DataErrors.EmptyArray("documents")))

                val idParsed = id.tryAmendmentId()
                    .doOnError { return failure(listOf(DataErrors.DataTypeMismatch("amendment.id"))) }
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

        override fun equals(other: Any?): Boolean = if (this === other)
            true
        else
            other is Amendment
                && this.id == other.id

        override fun hashCode(): Int = id.hashCode()

        class Document private constructor(
            val documentType: DocumentType,
            val id: String,
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
                        .doOnError { return failure(listOf(DataErrors.DataTypeMismatch("document.id"))) }
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
                other is Document
                    && this.id == other.id

            override fun hashCode(): Int = id.hashCode()
        }
    }
}


