package com.procurement.revision.domain.model

import com.procurement.revision.domain.functional.Result

class Ocid private constructor(val value: String) {
    companion object {
        private const val pattern = "^([a-z]{4})-([a-z0-9]{6})-([A-Z]{2})-([0-9]{13})-([A-Z]{2})-([0-9]{13})\$"

        fun tryCreate(ocid: String): Result<Ocid, String> {
            val regex = Regex(pattern)
            return if (ocid.matches(regex = regex))
                Result.success(Ocid(value = ocid))
            else
                Result.failure(pattern)
        }
    }
}