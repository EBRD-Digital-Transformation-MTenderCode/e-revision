package com.procurement.revision.application.model

import com.procurement.revision.domain.functional.ValidationResult
import com.procurement.revision.domain.functional.ValidationRule
import com.procurement.revision.domain.util.extension.getDuplicate
import com.procurement.revision.infrastructure.fail.error.DataErrors

fun <T : Collection<Any>?> noDuplicatesRule(attributeName: String): ValidationRule<T, DataErrors.Validation> =
    ValidationRule { received: T ->
        val duplicate = received?.getDuplicate { it }
        if (duplicate != null)
            ValidationResult.error(
                DataErrors.Validation.UniquenessDataMismatch(
                    value = duplicate.toString(), name = attributeName
                )
            )
        else
            ValidationResult.ok()
    }

fun <T : Collection<Any>?> notEmptyRule(attributeName: String): ValidationRule<T, DataErrors.Validation> =
    ValidationRule { received: T ->
        if (received != null && received.isEmpty())
            ValidationResult.error(DataErrors.Validation.EmptyArray(attributeName))
        else
            ValidationResult.ok()
    }