package com.procurement.revision.application.model.amendment

import com.procurement.revision.domain.enums.DocumentType
import com.procurement.revision.domain.functional.Option
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.functional.bind
import com.procurement.revision.domain.model.amendment.AmendmentId
import com.procurement.revision.domain.model.amendment.tryAmendmentId
import com.procurement.revision.domain.model.document.tryDocumentId
import com.procurement.revision.infrastructure.fail.Fail
import com.procurement.revision.infrastructure.fail.error.ValidationError
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
        ): Result<DataValidationParams, Fail> {
            if (amendments.isEmpty()) {
                return Result.failure(ValidationError.EmptyCollection("documents", "amendment"))
            }

            return OperationType.tryFromString(operationType)
                .bind { type ->
                    Result.success(
                        DataValidationParams(
                            cpid = cpid,
                            ocid = ocid,
                            operationType = type,
                            amendments = amendments
                        )
                    )
                }
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
            ): Result<Amendment, Fail> {
                if (documents.isDefined && documents.get.isEmpty())
                    return Result.failure(ValidationError.EmptyCollection("documents", "amendment"))

                val idResult = id.tryAmendmentId()
                if (idResult.isFail) return Result.failure(idResult.error)

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
                ): Result<Document, Fail> {

                    val idResult = id.tryDocumentId()
                    if (idResult.isFail) return Result.failure(idResult.error)

                    return DocumentType.tryFromString(documentType)
                        .bind { type ->
                            Result.success(
                                Document(
                                    id = idResult.get,
                                    description = description,
                                    documentType = type,
                                    title = title
                                )
                            )
                        }
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


