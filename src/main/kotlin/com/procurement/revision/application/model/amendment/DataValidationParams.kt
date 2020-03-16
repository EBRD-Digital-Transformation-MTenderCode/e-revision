package com.procurement.revision.application.model.amendment

import com.procurement.revision.domain.enums.DocumentType
import com.procurement.revision.domain.functional.Option
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.functional.Result.Companion.failure
import com.procurement.revision.domain.functional.Result.Companion.success
import com.procurement.revision.domain.model.Cpid
import com.procurement.revision.domain.model.Ocid
import com.procurement.revision.domain.model.amendment.AmendmentId
import com.procurement.revision.domain.model.amendment.tryAmendmentId
import com.procurement.revision.domain.model.document.tryDocumentId
import com.procurement.revision.infrastructure.fail.error.DataErrors
import com.procurement.revision.infrastructure.model.OperationType

class DataValidationParams private constructor(
    val amendments: List<Amendment>,
    val cpid: Cpid,
    val ocid: Ocid,
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
                return failure(DataErrors.Validation.EmptyArray("amendments"))
            }

            val operationTypeParsed = OperationType.orNull(operationType)
                ?: return failure(
                    DataErrors.Validation.UnknownValue(
                        name = "operationType",
                        expectedValues = OperationType.allowedValues,
                        actualValue = operationType
                    )
                )

            val cpidParsed = parseCpid(cpid)
                .doReturn { error -> return failure(error = error) }

            val ocidParsed = parseOcid(ocid)
                .doReturn { error -> return failure(error = error) }

            return success(
                DataValidationParams(
                    cpid = cpidParsed,
                    ocid = ocidParsed,
                    operationType = operationTypeParsed,
                    amendments = amendments
                )
            )
        }
    }

    class Amendment private constructor(
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
                    return failure(DataErrors.Validation.EmptyArray("documents"))

                val idParsed = id.tryAmendmentId()
                    .doOnError {
                        return failure(
                            DataErrors.Validation.DataFormatMismatch(
                                name = "amendment.id",
                                expectedFormat = "uuid",
                                actualValue = id
                            )
                        )
                    }
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
                ): Result<Document, DataErrors> {

                    val idParsed = id.tryDocumentId()
                        .doOnError {
                            return failure(
                                DataErrors.Validation.DataFormatMismatch(
                                    name = "document.id",
                                    actualValue = id,
                                    expectedFormat = "string"
                                )
                            )
                        }
                        .get

                    val documentTypeParsed = DocumentType.orNull(documentType)
                        ?: return failure(
                            DataErrors.Validation.UnknownValue(
                                name = "documentType",
                                actualValue = documentType,
                                expectedValues = DocumentType.allowedValues
                            )
                        )

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


