package com.procurement.revision.application.service

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.procurement.revision.application.model.amendment.GetAmendmentIdsParams
import com.procurement.revision.application.repository.AmendmentRepository
import com.procurement.revision.domain.enums.AmendmentRelatesTo
import com.procurement.revision.domain.enums.AmendmentStatus
import com.procurement.revision.domain.enums.AmendmentType
import com.procurement.revision.domain.enums.DocumentType
import com.procurement.revision.domain.model.amendment.Amendment
import com.procurement.revision.infrastructure.handler.GetAmendmentIdsHandler
import com.procurement.revision.infrastructure.repository.CassandraAmendmentRepository
import com.procurement.revision.infrastructure.service.GenerationService
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import java.time.LocalDateTime
import java.util.*

internal class AmendmentServiceTest {

    private lateinit var amendmentRepository: AmendmentRepository
    private lateinit var amendmentService: AmendmentService

    @BeforeEach
    fun init() {
        amendmentRepository = mock()
        amendmentService = AmendmentService(amendmentRepository, GenerationService())
    }

    private fun getTestAmendment() = Amendment(
        id = UUID.randomUUID(),
        token = UUID.randomUUID(),
        owner = "owner",
        date = LocalDateTime.now(),
        rationale = "rationale",
        description = "description",
        status = AmendmentStatus.PENDING,
        type = AmendmentType.CANCELLATION,
        relatesTo = AmendmentRelatesTo.LOT,
        relatedItem = UUID.randomUUID().toString(),
        documents = listOf(
            Amendment.Document(
                documentType = DocumentType.CANCELLATION_DETAILS,
                id = "id",
                title = "title",
                description = "description"
            )
        )
    )

    @Nested
    inner class GetAmendmentIdsBy {

        @Test
        fun getAmendmentIdsBySuccess() {
            val amendmentFirst = getTestAmendment()
            val amendmentSecond = getTestAmendment().copy(relatedItem = "someItem")
            whenever(amendmentRepository.findBy(any())).thenReturn(listOf(amendmentFirst, amendmentSecond))

            val actualIds = amendmentService.getAmendmentIdsBy(
                GetAmendmentIdsParams(
                    status = amendmentFirst.status,
                    relatesTo = amendmentFirst.relatesTo,
                    relatedItems = listOf(amendmentFirst.relatedItem),
                    cpid = "cpid",
                    ocid = "ocid",
                    type = amendmentFirst.type
                )
            )
            val expectedIds = listOf(amendmentFirst.id)

            assertEquals(expectedIds, actualIds)
        }

        @Test
        fun handleNonMatchingStatuses() {
            val amendment = getTestAmendment()
            whenever(amendmentRepository.findBy(any())).thenReturn(listOf(amendment))

            val nonMatchingStatus = AmendmentStatus.ACTIVE

            val actualIds = amendmentService.getAmendmentIdsBy(
                GetAmendmentIdsParams(
                    status = nonMatchingStatus,
                    relatesTo = amendment.relatesTo,
                    relatedItems = listOf(amendment.relatedItem),
                    cpid = "cpid",
                    ocid = "ocid",
                    type = amendment.type
                )
            )
            assertTrue(actualIds.isEmpty())
        }

        @Test
        fun handleNoStatus() {
            val amendmentFirst = getTestAmendment()
            val amendmentSecond = getTestAmendment().copy(status = AmendmentStatus.ACTIVE)
            whenever(amendmentRepository.findBy(any())).thenReturn(listOf(amendmentFirst, amendmentSecond))

            val actualIds = amendmentService.getAmendmentIdsBy(
                GetAmendmentIdsParams(
                    status = null,
                    relatesTo = amendmentFirst.relatesTo,
                    relatedItems = listOf(amendmentFirst.relatedItem, amendmentSecond.relatedItem),
                    cpid = "cpid",
                    ocid = "ocid",
                    type = amendmentFirst.type
                )
            ).sorted()

            val expectedIds = listOf(amendmentFirst.id, amendmentSecond.id).sorted()

            assertEquals(expectedIds, actualIds)
        }

        @Test
        fun handleNonMatchingRelatesTo() {
            val amendment = getTestAmendment()
            whenever(amendmentRepository.findBy(any())).thenReturn(listOf(amendment))

            val nonMatchingRelatesTo = AmendmentRelatesTo.CAN
            val actualIds = amendmentService.getAmendmentIdsBy(
                GetAmendmentIdsParams(
                    status = amendment.status,
                    relatesTo = nonMatchingRelatesTo,
                    relatedItems = listOf(amendment.relatedItem),
                    cpid = "cpid",
                    ocid = "ocid",
                    type = amendment.type
                )
            )
            assertTrue(actualIds.isEmpty())
        }

        @Test
        fun handleNoRelatesTo() {
            val amendmentFirst = getTestAmendment()
            val amendmentSecond = getTestAmendment().copy(relatesTo = AmendmentRelatesTo.CAN)
            whenever(amendmentRepository.findBy(any())).thenReturn(listOf(amendmentFirst, amendmentSecond))

            val actualIds = amendmentService.getAmendmentIdsBy(
                GetAmendmentIdsParams(
                    status = amendmentFirst.status,
                    relatesTo = null,
                    relatedItems = listOf(amendmentFirst.relatedItem, amendmentSecond.relatedItem),
                    cpid = "cpid",
                    ocid = "ocid",
                    type = amendmentFirst.type
                )
            ).sorted()

            val expectedIds = listOf(amendmentFirst.id, amendmentSecond.id).sorted()

            assertEquals(expectedIds, actualIds)
        }

        @Test
        fun handleNonMatchingRelatedItems() {
            val amendment = getTestAmendment()
            whenever(amendmentRepository.findBy(any())).thenReturn(listOf(amendment))

            val nonMatchingRelatedItems = listOf("someItem")
            val actualIds = amendmentService.getAmendmentIdsBy(
                GetAmendmentIdsParams(
                    status = amendment.status,
                    relatesTo = amendment.relatesTo,
                    relatedItems = nonMatchingRelatedItems,
                    cpid = "cpid",
                    ocid = "ocid",
                    type = amendment.type
                )
            )
            assertTrue(actualIds.isEmpty())
        }

        @Test
        fun handleNoRelatedItems() {
            val amendmentFirst = getTestAmendment()
            val amendmentSecond = getTestAmendment()
            whenever(amendmentRepository.findBy(any())).thenReturn(listOf(amendmentFirst, amendmentSecond))

            val actualIds = amendmentService.getAmendmentIdsBy(
                GetAmendmentIdsParams(
                    status = amendmentFirst.status,
                    relatesTo = null,
                    relatedItems = emptyList(),
                    cpid = "cpid",
                    ocid = "ocid",
                    type = amendmentFirst.type
                )
            ).sorted()

            val expectedIds = listOf(amendmentFirst.id, amendmentSecond.id).sorted()

            assertEquals(expectedIds, actualIds)
        }

        @Test
        fun handleNonMatchingType() {
            val amendment = getTestAmendment()
            whenever(amendmentRepository.findBy(any())).thenReturn(listOf(amendment))

            val nonMatchingType = AmendmentType.TENDER_CHANGE

            val actualIds = amendmentService.getAmendmentIdsBy(
                GetAmendmentIdsParams(
                    status = amendment.status,
                    relatesTo = amendment.relatesTo,
                    relatedItems = listOf(amendment.relatedItem),
                    cpid = "cpid",
                    ocid = "ocid",
                    type = nonMatchingType
                )
            )
            assertTrue(actualIds.isEmpty())
        }

        @Test
        fun handleNoType() {
            val amendmentFirst = getTestAmendment()
            val amendmentSecond = getTestAmendment().copy(type = AmendmentType.TENDER_CHANGE)
            whenever(amendmentRepository.findBy(any())).thenReturn(listOf(amendmentFirst, amendmentSecond))

            val actualIds = amendmentService.getAmendmentIdsBy(
                GetAmendmentIdsParams(
                    status = amendmentFirst.status,
                    relatesTo = null,
                    relatedItems = listOf(amendmentFirst.relatedItem, amendmentSecond.relatedItem),
                    cpid = "cpid",
                    ocid = "ocid",
                    type = null
                )
            ).sorted()

            val expectedIds = listOf(amendmentFirst.id, amendmentSecond.id).sorted()

            assertEquals(expectedIds, actualIds)
        }

        @Test
        fun handleNoParamsReceived() {
            val amendment = getTestAmendment()
            whenever(amendmentRepository.findBy(any())).thenReturn(listOf(amendment))

            val actualIds = amendmentService.getAmendmentIdsBy(
                GetAmendmentIdsParams(
                    status = null,
                    relatesTo = null,
                    relatedItems = emptyList(),
                    cpid = "cpid",
                    ocid = "ocid",
                    type = null
                )
            )
            val expectedIds = listOf(amendment.id)

            assertEquals(expectedIds, actualIds)
        }

        @Test
        fun handleDuplicateRelatedItems() {
            val amendment = getTestAmendment()
            val amendmentsInDb = listOf(amendment)
            whenever(amendmentRepository.findBy(any())).thenReturn(amendmentsInDb)

            val actualIds = amendmentService.getAmendmentIdsBy(
                GetAmendmentIdsParams(
                    status = amendment.status,
                    relatesTo = amendment.relatesTo,
                    relatedItems = listOf(amendment.relatedItem, amendment.relatedItem),
                    cpid = "cpid",
                    ocid = "ocid",
                    type = amendment.type
                )
            ).sorted()

            val expectedIds = amendmentsInDb.map { it.id }.sorted()

            assertEquals(expectedIds, actualIds)
        }
    }
}