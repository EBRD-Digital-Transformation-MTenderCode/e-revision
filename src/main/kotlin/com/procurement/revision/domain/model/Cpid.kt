package com.procurement.revision.domain.model

import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.functional.Result.Companion.failure
import com.procurement.revision.domain.functional.Result.Companion.success

class Cpid private constructor(val value: String) {

    companion object {
        private const val pattern = "^([a-z]{4})-([a-z0-9]{6})-([A-Z]{2})-[0-9]{13}\$"

        fun tryCreate(cpid: String): Result<Cpid, String> {
            val regex = Regex(pattern)
            return if (cpid.matches(regex = regex))
                success(Cpid(value = cpid))
            else
                failure(pattern)
        }
    }
}