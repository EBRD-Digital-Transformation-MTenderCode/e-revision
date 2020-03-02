package com.procurement.revision.infrastructure.converter

import com.procurement.revision.application.model.amendment.DataValidationParams
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.util.extension.mapOptionalResult
import com.procurement.revision.domain.util.extension.mapResult
import com.procurement.revision.infrastructure.fail.Fail
import com.procurement.revision.infrastructure.web.dto.request.amendment.DataValidationRequest

fun DataValidationRequest.convert(): Result<DataValidationParams, Fail> {
    val amendments = amendments.mapResult { it.convert() }
    if (amendments.isFail)
        return Result.failure(amendments.error)

    return DataValidationParams.tryCreate(
        amendments = amendments.get,
        ocid = ocid,
        cpid = cpid,
        operationType = operationType
    )
}

private fun DataValidationRequest.Amendment.convert(): Result<DataValidationParams.Amendment, Fail> {
    val documents = this.documents.mapOptionalResult { it.convert() }
    if (documents.isFail)
        return Result.failure(documents.error)

    return DataValidationParams.Amendment.tryCreate(
        rationale = this.rationale,
        description = this.description,
        documents = documents.get,
        id = this.id
    )
}

private fun DataValidationRequest.Amendment.Document.convert(): Result<DataValidationParams.Amendment.Document, Fail> =
    DataValidationParams.Amendment.Document.tryCreate(
        documentType = this.documentType,
        id = this.id,
        title = this.title,
        description = this.description
    )
