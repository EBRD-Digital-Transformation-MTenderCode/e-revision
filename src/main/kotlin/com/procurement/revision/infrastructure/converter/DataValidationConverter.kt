package com.procurement.revision.infrastructure.converter

import com.procurement.revision.application.model.amendment.DataValidationParams
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.util.extension.mapOptionalResult
import com.procurement.revision.domain.util.extension.mapResult
import com.procurement.revision.infrastructure.fail.error.DataErrors
import com.procurement.revision.infrastructure.web.dto.request.amendment.DataValidationRequest

fun DataValidationRequest.convert(): Result<DataValidationParams, DataErrors> {
    val amendments = amendments
        .mapResult { it.convert() }
        .doOnError { error -> return Result.failure(error) }
        .get

    return DataValidationParams.tryCreate(
        amendments = amendments,
        ocid = ocid,
        cpid = cpid,
        operationType = operationType
    )
}

private fun DataValidationRequest.Amendment.convert(): Result<DataValidationParams.Amendment, DataErrors> {
    val documents = this.documents
        .mapOptionalResult { it.convert() }
        .doOnError { error -> return Result.failure(error) }
        .get

    return DataValidationParams.Amendment.tryCreate(
        rationale = this.rationale,
        description = this.description,
        documents = documents,
        id = this.id
    )
}

private fun DataValidationRequest.Amendment.Document.convert(): Result<DataValidationParams.Amendment.Document, DataErrors> =
    DataValidationParams.Amendment.Document.tryCreate(
        documentType = this.documentType,
        id = this.id,
        title = this.title,
        description = this.description
    )
