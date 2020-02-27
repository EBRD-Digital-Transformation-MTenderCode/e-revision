package com.procurement.revision.domain.enums

import com.procurement.revision.domain.exception.EnumException
import com.procurement.revision.domain.util.Result
import com.procurement.revision.infrastructure.bind.databinding.Enumable
import com.procurement.revision.infrastructure.bind.databinding.Valuable
import com.procurement.revision.infrastructure.fail.error.RequestError

enum class DocumentType(override val text: String) : Valuable<DocumentType> {

    EVALUATION_CRITERIA("evaluationCriteria"),
    ELIGIBILITY_CRITERIA("eligibilityCriteria"),
    BILL_OF_QUANTITY("billOfQuantity"),
    ILLUSTRATION("illustration"),
    MARKET_STUDIES("marketStudies"),
    TENDER_NOTICE("tenderNotice"),
    BIDDING_DOCUMENTS("biddingDocuments"),
    PROCUREMENT_PLAN("procurementPlan"),
    TECHNICAL_SPECIFICATIONS("technicalSpecifications"),
    CONTRACT_DRAFT("contractDraft"),
    HEARING_NOTICE("hearingNotice"),
    CLARIFICATIONS("clarifications"),
    ENVIRONMENTAL_IMPACT("environmentalImpact"),
    ASSET_AND_LIABILITY_ASSESSMENT("assetAndLiabilityAssessment"),
    RISK_PROVISIONS("riskProvisions"),
    COMPLAINTS("complaints"),
    NEEDS_ASSESSMENT("needsAssessment"),
    FEASIBILITY_STUDY("feasibilityStudy"),
    PROJECT_PLAN("projectPlan"),
    CONFLICT_OF_INTEREST("conflictOfInterest"),
    CANCELLATION_DETAILS("cancellationDetails"),
    SHORTLISTED_FIRMS("shortlistedFirms"),
    EVALUATION_REPORTS("evaluationReports"),
    CONTRACT_ARRANGEMENTS("contractArrangements"),
    CONTRACT_GUARANTEES("contractGuarantees");

    override fun toString(): String = this.text

    companion object : Enumable<DocumentType> {
        private val elements: Map<String, DocumentType> = values().associateBy { it.text.toUpperCase() }

        override fun fromString(value: String): DocumentType = elements[value.toUpperCase()]
            ?: throw EnumException(
                enumType = DocumentType::class.java.canonicalName,
                value = value,
                values = values().joinToString { it.text }
            )

        fun tryFromString(value: String): Result<DocumentType, RequestError.EnumError> =
            elements[value.toUpperCase()]
                ?.let { Result.success(it) }
                ?: Result.failure(RequestError.EnumError(
                    enumType = DocumentType::class.java.canonicalName,
                    value = value,
                    values = values().joinToString { it.text }
                )
                )
    }
}
