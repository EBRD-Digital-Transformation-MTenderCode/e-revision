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
import com.procurement.revision.lib.toSetBy

class DataValidationParams private constructor(
    val amendment: Amendment,
    val cpid: Cpid,
    val ocid: Ocid,
    val operationType: OperationType
) {
    companion object {
        private val allowedOperationType = OperationType.values().filter { value ->
            when (value) {
                OperationType.LOT_CANCELLATION,
                OperationType.TENDER_CANCELLATION -> true
            }
        }.toSetBy { it.key }

        private val allowedDocumentType = DocumentType.values().filter { value ->
            when (value) {
                DocumentType.CANCELLATION_DETAILS -> true
                DocumentType.ASSET_AND_LIABILITY_ASSESSMENT,
                DocumentType.BIDDING_DOCUMENTS,
                DocumentType.BILL_OF_QUANTITY,
                DocumentType.CLARIFICATIONS,
                DocumentType.COMPLAINTS,
                DocumentType.CONFLICT_OF_INTEREST,
                DocumentType.CONTRACT_ARRANGEMENTS,
                DocumentType.CONTRACT_DRAFT,
                DocumentType.CONTRACT_GUARANTEES,
                DocumentType.ELIGIBILITY_CRITERIA,
                DocumentType.ENVIRONMENTAL_IMPACT,
                DocumentType.EVALUATION_CRITERIA,
                DocumentType.EVALUATION_REPORTS,
                DocumentType.FEASIBILITY_STUDY,
                DocumentType.HEARING_NOTICE,
                DocumentType.ILLUSTRATION,
                DocumentType.MARKET_STUDIES,
                DocumentType.NEEDS_ASSESSMENT,
                DocumentType.PROCUREMENT_PLAN,
                DocumentType.PROJECT_PLAN,
                DocumentType.RISK_PROVISIONS,
                DocumentType.SHORTLISTED_FIRMS,
                DocumentType.TECHNICAL_SPECIFICATIONS,
                DocumentType.TENDER_NOTICE -> false
            }
        }.toSetBy { it.key }

        fun tryCreate(
            amendment: Amendment,
            cpid: String,
            ocid: String,
            operationType: String
        ): Result<DataValidationParams, DataErrors> {
            val operationTypeParsed = operationType.takeIf { it in OperationType }
                ?.also {
                    if (it !in allowedOperationType) {
                        return failure(
                            DataErrors.Validation.UnknownValue(
                                name = "operationType",
                                expectedValues = allowedOperationType,
                                actualValue = it
                            )
                        )
                    }
                }?.let { OperationType.creator(it) }
                ?: return failure(
                    DataErrors.Validation.UnknownValue(
                        name = "operationType",
                        actualValue = operationType,
                        expectedValues = OperationType.allowedValues
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
                    amendment = amendment
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

                    val documentTypeParsed = documentType.takeIf { it in DocumentType }
                        ?.also {
                            if (it !in allowedDocumentType)
                                return failure(
                                    DataErrors.Validation.UnknownValue(
                                        name = "documentType",
                                        actualValue = documentType,
                                        expectedValues = allowedDocumentType
                                    )
                                )
                        }
                        ?.let { DocumentType.creator(it) }
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


