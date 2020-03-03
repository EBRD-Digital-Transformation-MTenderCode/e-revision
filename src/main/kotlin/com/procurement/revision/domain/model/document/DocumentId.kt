package com.procurement.revision.domain.model.document

import com.procurement.revision.domain.functional.Result
import com.procurement.revision.infrastructure.fail.error.ParsingError

typealias DocumentId = String

fun String.tryDocumentId(): Result<DocumentId, ParsingError> =
    Result.success(this)