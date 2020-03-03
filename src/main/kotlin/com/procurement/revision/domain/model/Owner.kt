package com.procurement.revision.domain.model

import com.procurement.revision.domain.functional.Result
import com.procurement.revision.infrastructure.fail.error.ParsingError

typealias Owner = String

fun String.tryOwner(): Result<Owner, ParsingError> =
    Result.success(this)