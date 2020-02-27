package com.procurement.revision.application.model.amendment

import com.procurement.revision.domain.enums.DocumentType
import com.procurement.revision.domain.util.Option
import com.procurement.revision.domain.util.Result
import com.procurement.revision.domain.util.flatMap
import com.procurement.revision.infrastructure.fail.Fail
import com.procurement.revision.infrastructure.fail.error.ValidationError
import com.procurement.revision.infrastructure.model.OperationType
import java.util.*

data class DataValidationParams private constructor(
    val amendments: List<Amendment>,
    val cpid: String,
    val ocid: String,
    val operationType: OperationType
) {
    companion object {
        fun create(
            amendments: List<Amendment>,
            cpid: String,
            ocid: String,
            operationType: String
        ): Result<DataValidationParams, Fail> {
            if (amendments.isEmpty()) {
                return Result.failure(ValidationError.EmptyCollection("documents", "amendment"))
            }

            return OperationType.tryFromString(operationType)
                .flatMap { type ->
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
        val id: UUID,
        val rationale: String,
        val description: String?,
        val documents: List<Document>
    ) {
        companion object {
            fun create(
                id: UUID,
                rationale: String,
                description: String?,
                documents: Option<List<Document>>
            ): Result<Amendment, Fail> {
                if (documents.isDefined && documents.get.isEmpty())
                    return Result.failure(ValidationError.EmptyCollection("documents", "amendment"))

                return Result.success(
                    Amendment(
                        id = id,
                        rationale = rationale,
                        description = description,
                        documents = if (documents.isDefined) documents.get else emptyList()
                    )
                )
            }

            /*fun create(
                rationale: String,
                description: String?,
                documents: Result<List<Document>?, Fail>
            ): Result<Amendment, Fail> {
                val docs = when(documents) {
                    is Result.Success -> documents.get
                    is Result.Failure -> return Result.failure(documents.error)
                }
                if(docs != null && docs.isEmpty())
                    return Result.failure(ValidationError.EmptyCollection("documents", "amendment"))

                return Result.success(
                    Amendment(
                        rationale = rationale,
                        description = description,
                        documents = docs ?: emptyList()
                    )
                )
            }*/
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
                ): Result<Document, Fail> = DocumentType.tryFromString(documentType)
                    .flatMap { type ->
                        Result.success(
                            Document(
                                id = id,
                                description = description,
                                documentType = type,
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


