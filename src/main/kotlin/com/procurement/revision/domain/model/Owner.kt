package com.procurement.revision.domain.model

import com.procurement.revision.domain.functional.Result

typealias Owner = String

fun String.tryOwner(): Result<Owner, String> =
    Result.success(this)