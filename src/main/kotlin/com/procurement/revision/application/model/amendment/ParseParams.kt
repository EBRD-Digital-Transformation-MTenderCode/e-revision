package com.procurement.revision.application.model.amendment

import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.functional.asSuccess
import com.procurement.revision.domain.model.Cpid
import com.procurement.revision.domain.model.Ocid
import com.procurement.revision.infrastructure.fail.error.DataErrors

fun parseCpid(value: String): Result<Cpid, DataErrors.Validation.DataMismatchToPattern> =
    Cpid.tryCreate(cpid = value)
        .doReturn { expectedPattern ->
            return Result.failure(
                DataErrors.Validation.DataMismatchToPattern(
                    name = "cpid",
                    pattern = expectedPattern,
                    actualValue = value
                )
            )
        }
        .asSuccess()

fun parseOcid(value: String): Result<Ocid, DataErrors.Validation.DataMismatchToPattern> =
    Ocid.tryCreate(ocid = value)
        .doReturn { expectedPattern ->
            return Result.failure(
                DataErrors.Validation.DataMismatchToPattern(
                    name = "ocid",
                    pattern = expectedPattern,
                    actualValue = value
                )
            )
        }
        .asSuccess()