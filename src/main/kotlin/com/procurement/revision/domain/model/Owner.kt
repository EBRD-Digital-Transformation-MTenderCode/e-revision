package com.procurement.revision.domain.model

import com.procurement.revision.domain.functional.Result
import com.procurement.revision.infrastructure.fail.error.RequestError

typealias Owner = String

fun String.tryOwner(): Result<Owner, RequestError.ParsingError> =
    Result.success(this)