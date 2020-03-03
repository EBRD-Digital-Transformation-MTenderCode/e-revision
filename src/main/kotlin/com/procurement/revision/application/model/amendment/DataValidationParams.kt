package com.procurement.revision.application.model.amendment

import com.procurement.revision.domain.enums.DocumentType
import com.procurement.revision.domain.functional.Option
import com.procurement.revision.domain.functional.Result
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
        ): Result<DataValidationParams, DataErrors> {
            if (amendments.isEmpty()) {
                return Result.failure(DataErrors.EmptyArray("amendments"))
            }

            val operationTypeResult = OperationType.tryFromString(operationType)
            if (operationTypeResult.isFail) return Result.failure(DataErrors.DataTypeMismatch("operationType"))

            return Result.success(
                DataValidationParams(
                    cpid = cpid,
                    ocid = ocid,
                    operationType = operationTypeResult.get,
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
            ): Result<Amendment, DataErrors> {
                if (documents.isDefined && documents.get.isEmpty())
                    return Result.failure(DataErrors.EmptyArray("documents"))

                val idResult = id.tryAmendmentId()
                if (idResult.isFail) return Result.failure(DataErrors.DataTypeMismatch("amendment.id"))

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
                ): Result<Document, DataErrors> {

                    val idResult = id.tryDocumentId()
                    if (idResult.isFail) return Result.failure(DataErrors.DataTypeMismatch("document.id"))

                    val documentTypeResult = DocumentType.tryFromString(documentType)
                    if (documentTypeResult.isFail) return Result.failure(DataErrors.DataTypeMismatch("documentType"))

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
                other is Document
                    && this.id == other.id

            override fun hashCode(): Int = id.hashCode()
        }
    }
}


