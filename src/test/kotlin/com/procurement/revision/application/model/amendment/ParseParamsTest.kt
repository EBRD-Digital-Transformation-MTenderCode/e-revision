package com.procurement.revision.application.model.amendment

import com.procurement.revision.domain.enums.AmendmentStatus
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ParseParamsTest {

    companion object {
        private const val AMENDMENT_STATUS_ATTRIBUTE_NAME: String = "amendment.status"
    }

    @Nested
    inner class ParseAmendmentStatus {
        @Test
        fun invalidStatus_fail() {
            val status = "randomString"
            val allowedStatuses = AmendmentStatus.allowedElements.toSet()
            val actualResult = parseAmendmentStatus(
                status = status,
                attributeName = AMENDMENT_STATUS_ATTRIBUTE_NAME,
                allowedStatuses = allowedStatuses
            )

            assertTrue(actualResult.isFail)
        }

        @Test
        fun success() {
            val statusExpected = AmendmentStatus.allowedElements.first()
            val allowedStatuses = AmendmentStatus.allowedElements.toSet()
            val actualResult = parseAmendmentStatus(
                status = statusExpected.key,
                attributeName = AMENDMENT_STATUS_ATTRIBUTE_NAME,
                allowedStatuses = allowedStatuses
            )

            assertTrue(statusExpected == actualResult.get)
        }

        @Test
        fun unallowedStatus_fail() {
            val statusExpected = AmendmentStatus.allowedElements.first()
            val allowedStatuses = AmendmentStatus.allowedElements.toSet() - statusExpected
            val actualResult = parseAmendmentStatus(
                status = statusExpected.key,
                attributeName = AMENDMENT_STATUS_ATTRIBUTE_NAME,
                allowedStatuses = allowedStatuses
            )

            assertTrue(actualResult.isFail)
        }
    }
}