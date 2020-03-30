package com.procurement.revision.application.model.amendment

import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.functional.asSuccess
import com.procurement.revision.domain.model.Cpid
import com.procurement.revision.domain.model.Ocid
import com.procurement.revision.domain.model.Owner
import com.procurement.revision.domain.model.Token
import com.procurement.revision.domain.model.amendment.AmendmentId
import com.procurement.revision.domain.model.amendment.tryAmendmentId
import com.procurement.revision.domain.model.tryOwner
import com.procurement.revision.domain.model.tryToken
import com.procurement.revision.infrastructure.fail.error.DataErrors

fun parseCpid(value: String): Result<Cpid, DataErrors.Validation.DataMismatchToPattern> =
    Cpid.tryCreateOrNull(value = value)
        ?.asSuccess()
        ?: Result.failure(
            DataErrors.Validation.DataMismatchToPattern(
                name = "cpid",
                pattern = Cpid.pattern,
                actualValue = value
            )
        )

fun parseOcid(value: String): Result<Ocid, DataErrors.Validation.DataMismatchToPattern> =
    Ocid.tryCreateOrNull(value = value)
        ?.asSuccess()
        ?: Result.failure(
            DataErrors.Validation.DataMismatchToPattern(
                name = "ocid",
                pattern = Ocid.pattern,
                actualValue = value
            )
        )

fun parseAmendmentId(value: String): Result<AmendmentId, DataErrors.Validation.DataFormatMismatch> =
    value.tryAmendmentId()
        .doReturn {
            return Result.failure(
                DataErrors.Validation.DataFormatMismatch(
                    name = "amendmentId",
                    expectedFormat = "uuid",
                    actualValue = value
                )
            )
        }.asSuccess()

fun parseToken(value: String): Result<Token, DataErrors.Validation.DataFormatMismatch> =
    value.tryToken()
        .doReturn {
            return Result.failure(
                DataErrors.Validation.DataFormatMismatch(
                    name = "token",
                    expectedFormat = "uuid",
                    actualValue = value
                )
            )
        }.asSuccess()

fun parseOwner(value: String): Result<Owner, DataErrors.Validation.DataFormatMismatch> =
    value.tryOwner()
        .doReturn {
            return Result.failure(
                DataErrors.Validation.DataFormatMismatch(
                    name = "owner",
                    expectedFormat = "uuid",
                    actualValue = value
                )
            )
        }.asSuccess()

