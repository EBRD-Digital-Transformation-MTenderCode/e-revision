package com.procurement.revision.domain.model.document

import com.procurement.revision.domain.functional.Result
import com.procurement.revision.infrastructure.fail.error.RequestError

typealias DocumentId = String

fun String.tryDocumentId(): Result<DocumentId, RequestError.ParsingError> =
    Result.success(this)