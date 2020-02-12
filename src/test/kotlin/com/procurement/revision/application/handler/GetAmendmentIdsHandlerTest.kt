package com.procurement.revision.application.handler

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.procurement.revision.application.model.amendment.GetAmendmentIdsData
import com.procurement.revision.domain.enums.AmendmentRelatesTo
import com.procurement.revision.domain.enums.AmendmentStatus
import com.procurement.revision.domain.enums.AmendmentType
import com.procurement.revision.domain.enums.DocumentType
import com.procurement.revision.domain.model.amendment.Amendment
import com.procurement.revision.infrastructure.repository.CassandraAmendmentRepository
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.time.LocalDateTime
import java.util.*

internal class GetAmendmentIdsHandlerTest {
    private lateinit var cassandraAmendmentRepository: CassandraAmendmentRepository
    private lateinit var getAmendmentIdsHandler: GetAmendmentIdsHandler

    @BeforeEach
    fun init() {
        cassandraAmendmentRepository = mock()
        getAmendmentIdsHandler = GetAmendmentIdsHandler(cassandraAmendmentRepository)
    }

    fun getTestAmendment() = Amendment(
        id = UUID.randomUUID(),
        token = UUID.randomUUID(),
        owner = "owner",
        date = LocalDateTime.now(),
        rationale = "rationale",
        description = "description",
        status = AmendmentStatus.PENDING,
        type = AmendmentType.CANCELLATION,
        relatesTo = AmendmentRelatesTo.LOT,
        relatedItem = "relatedItem",
        documents = listOf(
            Amendment.Document(
                documentType = DocumentType.CANCELLATION_DETAILS,
                id = "id",
                title = "title",
                description = "description"
            )
        )
    )

    @Test
    fun handleSuccess() {
        val amendment = getTestAmendment()
        whenever(cassandraAmendmentRepository.findBy(any())).thenReturn(listOf(amendment))

        val result = getAmendmentIdsHandler.handle(
            GetAmendmentIdsData(
                status = amendment.status,
                relatesTo = amendment.relatesTo,
                relatedItems = listOf(amendment.relatedItem),
                cpid = "cpid",
                ocid = "ocid",
                type = amendment.type
            )
        )

        val expectedIds = listOf(amendment.id)
        val actualIds = result.map { it.id }

        assertEquals(expectedIds, actualIds)
    }

    @Test
    fun handleNonMatchingStatuses() {
        val amendment = getTestAmendment()
        whenever(cassandraAmendmentRepository.findBy(any())).thenReturn(listOf(amendment))

        val nonMatchingStatus = AmendmentStatus.ACTIVE

        val result = getAmendmentIdsHandler.handle(
            GetAmendmentIdsData(
                status = nonMatchingStatus,
                relatesTo = amendment.relatesTo,
                relatedItems = listOf(amendment.relatedItem),
                cpid = "cpid",
                ocid = "ocid",
                type = amendment.type
            )
        )

        val actualIds = result.map { it.id }
        assertTrue(actualIds.isEmpty())
    }

    @Test
    fun handleNoStatus() {
        val amendment = getTestAmendment()
        whenever(cassandraAmendmentRepository.findBy(any())).thenReturn(listOf(amendment))

        val result = getAmendmentIdsHandler.handle(
            GetAmendmentIdsData(
                status = null,
                relatesTo = amendment.relatesTo,
                relatedItems = listOf(amendment.relatedItem),
                cpid = "cpid",
                ocid = "ocid",
                type = amendment.type
            )
        )
        val expectedIds = listOf(amendment.id)
        val actualIds = result.map { it.id }

        assertEquals(expectedIds, actualIds)
    }

    @Test
    fun handleNonMatchingRelatesTo() {
        val amendment = getTestAmendment()
        whenever(cassandraAmendmentRepository.findBy(any())).thenReturn(listOf(amendment))

        val nonMatchingRelatesTo = AmendmentRelatesTo.CAN
        val result = getAmendmentIdsHandler.handle(
            GetAmendmentIdsData(
                status = amendment.status,
                relatesTo = nonMatchingRelatesTo,
                relatedItems = listOf(amendment.relatedItem),
                cpid = "cpid",
                ocid = "ocid",
                type = amendment.type
            )
        )

        val actualIds = result.map { it.id }
        assertTrue(actualIds.isEmpty())
    }

    @Test
    fun handleNoRelatesTo() {
        val amendment = getTestAmendment()
        whenever(cassandraAmendmentRepository.findBy(any())).thenReturn(listOf(amendment))

        val result = getAmendmentIdsHandler.handle(
            GetAmendmentIdsData(
                status = amendment.status,
                relatesTo = null,
                relatedItems = listOf(amendment.relatedItem),
                cpid = "cpid",
                ocid = "ocid",
                type = amendment.type
            )
        )
        val expectedIds = listOf(amendment.id)
        val actualIds = result.map { it.id }

        assertEquals(expectedIds, actualIds)
    }

    @Test
    fun handleNonMatchingRelatedItems() {
        val amendment = getTestAmendment()
        whenever(cassandraAmendmentRepository.findBy(any())).thenReturn(listOf(amendment))

        val nonMatchingRelatedItems = listOf("someItem")
        val result = getAmendmentIdsHandler.handle(
            GetAmendmentIdsData(
                status = amendment.status,
                relatesTo = amendment.relatesTo,
                relatedItems = nonMatchingRelatedItems,
                cpid = "cpid",
                ocid = "ocid",
                type = amendment.type
            )
        )

        val actualIds = result.map { it.id }
        assertTrue(actualIds.isEmpty())
    }

    @Test
    fun handleNoRelatedItems() {
        val amendment = getTestAmendment()
        whenever(cassandraAmendmentRepository.findBy(any())).thenReturn(listOf(amendment))

        val result = getAmendmentIdsHandler.handle(
            GetAmendmentIdsData(
                status = amendment.status,
                relatesTo = amendment.relatesTo,
                relatedItems = emptyList(),
                cpid = "cpid",
                ocid = "ocid",
                type = amendment.type
            )
        )
        val expectedIds = listOf(amendment.id)
        val actualIds = result.map { it.id }

        assertEquals(expectedIds, actualIds)
    }

    @Test
    fun handleNonMatchingType() {
        val amendment = getTestAmendment()
        whenever(cassandraAmendmentRepository.findBy(any())).thenReturn(listOf(amendment))

        val nonMatchingType = AmendmentType.TENDER_CHANGE

        val result = getAmendmentIdsHandler.handle(
            GetAmendmentIdsData(
                status = amendment.status,
                relatesTo = amendment.relatesTo,
                relatedItems = listOf(amendment.relatedItem),
                cpid = "cpid",
                ocid = "ocid",
                type = nonMatchingType
            )
        )

        val actualIds = result.map { it.id }
        assertTrue(actualIds.isEmpty())
    }

    @Test
    fun handleNoType() {
        val amendment = getTestAmendment()
        whenever(cassandraAmendmentRepository.findBy(any())).thenReturn(listOf(amendment))

        val result = getAmendmentIdsHandler.handle(
            GetAmendmentIdsData(
                status = amendment.status,
                relatesTo = amendment.relatesTo,
                relatedItems = listOf(amendment.relatedItem),
                cpid = "cpid",
                ocid = "ocid",
                type = null
            )
        )
        val expectedIds = listOf(amendment.id)
        val actualIds = result.map { it.id }

        assertEquals(expectedIds, actualIds)
    }

    @Test
    fun handleNoParamsReceived() {
        val amendment = getTestAmendment()
        whenever(cassandraAmendmentRepository.findBy(any())).thenReturn(listOf(amendment))

        val result = getAmendmentIdsHandler.handle(
            GetAmendmentIdsData(
                status = null,
                relatesTo = null,
                relatedItems = emptyList(),
                cpid = "cpid",
                ocid = "ocid",
                type = null
            )
        )
        val expectedIds = listOf(amendment.id)
        val actualIds = result.map { it.id }

        assertEquals(expectedIds, actualIds)
    }

    @Test
    fun handleFewAmendments() {
        val amendment = getTestAmendment()
        val secondAmendment = amendment.copy(id = UUID.randomUUID(), description = "secondAmendment")

        val nonMatchingStatus = AmendmentStatus.WITHDRAWN
        val thirdAmendment = amendment.copy(
            id = UUID.randomUUID(),
            description = "thirdAmendment",
            status = nonMatchingStatus
        )
        val amendmentsInDb = listOf(amendment, secondAmendment, thirdAmendment)
        whenever(cassandraAmendmentRepository.findBy(any())).thenReturn(amendmentsInDb)

        val result = getAmendmentIdsHandler.handle(
            GetAmendmentIdsData(
                status = amendment.status,
                relatesTo = amendment.relatesTo,
                relatedItems = listOf(amendment.relatedItem),
                cpid = "cpid",
                ocid = "ocid",
                type = amendment.type
            )
        )

        val expectedIds = listOf(amendment, secondAmendment).map { it.id }.sorted()
        val actualIds = result.map { it.id }.sorted()

        assertEquals(expectedIds, actualIds)
    }

    @Test
    fun handleFewRelatedItems() {
        val amendment = getTestAmendment()
        val secondAmendment = amendment.copy(id = UUID.randomUUID(), description = "secondAmendment")

        val thirdAmendment = amendment.copy(
            id = UUID.randomUUID(),
            description = "thirdAmendment",
            relatedItem = "relatedItem3"
        )
        val amendmentsInDb = listOf(amendment, secondAmendment, thirdAmendment)
        whenever(cassandraAmendmentRepository.findBy(any())).thenReturn(amendmentsInDb)

        val result = getAmendmentIdsHandler.handle(
            GetAmendmentIdsData(
                status = amendment.status,
                relatesTo = amendment.relatesTo,
                relatedItems = listOf(amendment.relatedItem, thirdAmendment.relatedItem),
                cpid = "cpid",
                ocid = "ocid",
                type = amendment.type
            )
        )

        val expectedIds = amendmentsInDb.map { it.id }.sorted()
        val actualIds = result.map { it.id }.sorted()

        assertEquals(expectedIds, actualIds)
    }

    @Test
    fun handleFewRelatedItemsWithDifferentRelatesTo() {
        val amendment = getTestAmendment()
        val secondAmendment = amendment.copy(id = UUID.randomUUID(), description = "secondAmendment")

        val nonMatchingRelatesTo = AmendmentRelatesTo.CAN
        val thirdAmendment = amendment.copy(
            id = UUID.randomUUID(),
            description = "thirdAmendment",
            relatedItem = "relatedItem3",
            relatesTo = nonMatchingRelatesTo
        )
        val amendmentsInDb = listOf(amendment, secondAmendment, thirdAmendment)
        whenever(cassandraAmendmentRepository.findBy(any())).thenReturn(amendmentsInDb)

        val result = getAmendmentIdsHandler.handle(
            GetAmendmentIdsData(
                status = amendment.status,
                relatesTo = amendment.relatesTo,
                relatedItems = listOf(amendment.relatedItem, thirdAmendment.relatedItem),
                cpid = "cpid",
                ocid = "ocid",
                type = amendment.type
            )
        )

        val expectedIds = listOf(amendment, secondAmendment).map { it.id }.sorted()
        val actualIds = result.map { it.id }.sorted()

        assertEquals(expectedIds, actualIds)
    }

    @Test
    fun handleDuplicateRelatedItems() {
        val amendment = getTestAmendment()
        val amendmentsInDb = listOf(amendment)
        whenever(cassandraAmendmentRepository.findBy(any())).thenReturn(amendmentsInDb)

        val result = getAmendmentIdsHandler.handle(
            GetAmendmentIdsData(
                status = amendment.status,
                relatesTo = amendment.relatesTo,
                relatedItems = listOf(amendment.relatedItem, amendment.relatedItem),
                cpid = "cpid",
                ocid = "ocid",
                type = amendment.type
            )
        )

        val expectedIds = amendmentsInDb.map { it.id }.sorted()
        val actualIds = result.map { it.id }.sorted()

        assertEquals(expectedIds, actualIds)
    }


}