package com.procurement.revision.domain.model.document

import com.procurement.revision.domain.functional.Result

typealias DocumentId = String

fun String.tryDocumentId(): Result<DocumentId, String> =
    Result.success(this)