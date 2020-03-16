package com.procurement.revision.application.model.amendment

import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.functional.asSuccess
import com.procurement.revision.domain.model.Cpid
import com.procurement.revision.domain.model.Ocid
import com.procurement.revision.infrastructure.fail.error.DataErrors

fun parseCpid(value: String, name: String = "cpid"): Result<Cpid, DataErrors.Validation.DataMismatchToPattern> =
    Cpid.tryCreate(cpid = value)
        .doReturn { expectedPattern ->
            return Result.failure(
                DataErrors.Validation.DataMismatchToPattern(
                    name = name,
                    pattern = expectedPattern,
                    actualValue = value
                )
            )
        }
        .asSuccess()

fun parseOcid(value: String, name: String = "ocid"): Result<Ocid, DataErrors.Validation.DataMismatchToPattern> =
    Ocid.tryCreate(ocid = value)
        .doReturn { expectedPattern ->
            return Result.failure(
                DataErrors.Validation.DataMismatchToPattern(
                    name = name,
                    pattern = expectedPattern,
                    actualValue = value
                )
            )
        }
        .asSuccess()