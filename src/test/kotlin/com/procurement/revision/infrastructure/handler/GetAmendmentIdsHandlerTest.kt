package com.procurement.revision.infrastructure.handler

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.procurement.revision.domain.enums.AmendmentRelatesTo
import com.procurement.revision.domain.enums.AmendmentStatus
import com.procurement.revision.domain.enums.AmendmentType
import com.procurement.revision.domain.enums.DocumentType
import com.procurement.revision.domain.model.amendment.Amendment
import com.procurement.revision.infrastructure.repository.CassandraAmendmentRepository
import com.procurement.revision.infrastructure.web.dto.ApiVersion
import com.procurement.revision.infrastructure.web.dto.ResponseStatus
import com.procurement.revision.json.loadJson
import com.procurement.revision.json.toNode
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.empty
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

internal class GetAmendmentIdsHandlerTest {
    private lateinit var cassandraAmendmentRepository: CassandraAmendmentRepository
    private lateinit var getAmendmentIdsHandler: GetAmendmentIdsHandler

    companion object {
        private const val AMENDMENT_REQUEST_JSON = "json/dto/getAmendmentIds/get_amendment_id_request_full.json"
        private const val AMENDMENT_REQUEST_NO_STATUS_JSON = "json/dto/getAmendmentIds/get_amendment_id_request_no_status.json"
        private const val AMENDMENT_REQUEST_NO_RELATES_TO_JSON = "json/dto/getAmendmentIds/get_amendment_id_request_no_relates_to.json"
        private const val AMENDMENT_REQUEST_NO_RELATED_ITEMS_JSON = "json/dto/getAmendmentIds/get_amendment_id_request_no_related_items.json"
        private const val AMENDMENT_REQUEST_NO_TYPE_JSON = "json/dto/getAmendmentIds/get_amendment_id_request_no_type.json"
        private const val AMENDMENT_REQUEST_NO_OPTIONAL_PARAMS_JSON = "json/dto/getAmendmentIds/get_amendment_id_request_no_optional_params.json"
        private const val AMENDMENT_REQUEST_DUPLICATE_ITEMS_JSON = "json/dto/getAmendmentIds/get_amendment_id_request_duplicate_items.json"
        private const val REQUEST_ID =  "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        private const val REQUEST_VERSION =  "2.0.0"
    }

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
        relatedItem = "5407df26-9305-4f1b-b438-8e16d2ab2201",
        documents = listOf(
            Amendment.Document(
                documentType = DocumentType.CANCELLATION_DETAILS,
                id = "id",
                title = "title",
                description = "description"
            )
        )
    )

    fun getNode(path: String) = loadJson(path).toNode()

    @Test
    fun handleSuccess() {
        val amendmentFirst = getTestAmendment()
        val amendmentSecond = getTestAmendment().copy(
            relatedItem = "nonMatchingItem"
        )

        whenever(cassandraAmendmentRepository.findBy(any())).thenReturn(listOf(amendmentFirst, amendmentSecond))

        val node = getNode(AMENDMENT_REQUEST_JSON)
        val actualApiResponse = getAmendmentIdsHandler.handle(node)

        val expectedAmendmentIds = listOf(amendmentFirst.id)
        val expectedStatus = ResponseStatus.SUCCESS
        val expectedId = UUID.fromString(REQUEST_ID)
        val expectedVersion = ApiVersion.valueOf(REQUEST_VERSION)

        assertEquals(expectedAmendmentIds, actualApiResponse.result)
        assertEquals(expectedStatus, actualApiResponse.status)
        assertEquals(expectedId, actualApiResponse.id)
        assertEquals(expectedVersion, actualApiResponse.version)
    }

    @Test
    fun handleNonMatchingStatuses() {
        val amendment = getTestAmendment().copy(
            status = AmendmentStatus.WITHDRAWN
        )
        whenever(cassandraAmendmentRepository.findBy(any())).thenReturn(listOf(amendment))
        val node = getNode(AMENDMENT_REQUEST_JSON)

        val actualApiResponse = getAmendmentIdsHandler.handle(node)

        val expectedStatus = ResponseStatus.SUCCESS
        val expectedId = UUID.fromString(REQUEST_ID)
        val expectedVersion = ApiVersion.valueOf(REQUEST_VERSION)

        assertThat(actualApiResponse.result as List<*>, `is`((empty())))
        assertEquals(expectedStatus, actualApiResponse.status)
        assertEquals(expectedId, actualApiResponse.id)
        assertEquals(expectedVersion, actualApiResponse.version)
    }

    @Test
    fun handleNoStatus() {
        val amendmentFirst = getTestAmendment().copy(
            id = UUID.randomUUID(),
            status = AmendmentStatus.WITHDRAWN
        )
        val amendmentSecond = getTestAmendment().copy(
            id = UUID.randomUUID(),
            relatedItem = "99368915-668e-4ea6-9374-f15a8eb79521",
            status = AmendmentStatus.ACTIVE
        )
        whenever(cassandraAmendmentRepository.findBy(any())).thenReturn(listOf(amendmentFirst, amendmentSecond))

        val node = getNode(AMENDMENT_REQUEST_NO_STATUS_JSON)
        val actualApiResponse = getAmendmentIdsHandler.handle(node)
        val actualAmendmentIds = (actualApiResponse.result as List<String>).sorted()

        val expectedAmendmentIds = listOf(amendmentFirst.id, amendmentSecond.id).sorted()
        val expectedStatus = ResponseStatus.SUCCESS
        val expectedId = UUID.fromString(REQUEST_ID)
        val expectedVersion = ApiVersion.valueOf(REQUEST_VERSION)

        assertEquals(expectedAmendmentIds, actualAmendmentIds)
        assertEquals(expectedStatus, actualApiResponse.status)
        assertEquals(expectedId, actualApiResponse.id)
        assertEquals(expectedVersion, actualApiResponse.version)
    }

    @Test
    fun handleNonMatchingRelatesTo() {
        val amendment = getTestAmendment().copy(
            relatesTo = AmendmentRelatesTo.TENDER
        )
        whenever(cassandraAmendmentRepository.findBy(any())).thenReturn(listOf(amendment))
        val node = getNode(AMENDMENT_REQUEST_JSON)

        val actualApiResponse = getAmendmentIdsHandler.handle(node)

        val expectedStatus = ResponseStatus.SUCCESS
        val expectedId = UUID.fromString(REQUEST_ID)
        val expectedVersion = ApiVersion.valueOf(REQUEST_VERSION)

        assertThat(actualApiResponse.result as List<*>, `is`((empty())))
        assertEquals(expectedStatus, actualApiResponse.status)
        assertEquals(expectedId, actualApiResponse.id)
        assertEquals(expectedVersion, actualApiResponse.version)
    }

    @Test
    fun handleNoRelatesTo() {
        val amendmentFirst = getTestAmendment().copy(
            id = UUID.randomUUID(),
            relatesTo = AmendmentRelatesTo.CAN
        )
        val amendmentSecond = getTestAmendment().copy(
            id = UUID.randomUUID(),
            relatedItem = "99368915-668e-4ea6-9374-f15a8eb79521",
            relatesTo = AmendmentRelatesTo.TENDER
        )
        whenever(cassandraAmendmentRepository.findBy(any())).thenReturn(listOf(amendmentFirst, amendmentSecond))

        val node = getNode(AMENDMENT_REQUEST_NO_RELATES_TO_JSON)
        val actualApiResponse = getAmendmentIdsHandler.handle(node)
        val actualAmendmentIds = (actualApiResponse.result as List<String>).sorted()

        val expectedAmendmentIds = listOf(amendmentFirst.id, amendmentSecond.id).sorted()
        val expectedStatus = ResponseStatus.SUCCESS
        val expectedId = UUID.fromString(REQUEST_ID)
        val expectedVersion = ApiVersion.valueOf(REQUEST_VERSION)

        assertEquals(expectedAmendmentIds, actualAmendmentIds)
        assertEquals(expectedStatus, actualApiResponse.status)
        assertEquals(expectedId, actualApiResponse.id)
        assertEquals(expectedVersion, actualApiResponse.version)
    }

    @Test
    fun handleNonMatchingRelatedItems() {
        val amendment = getTestAmendment().copy(relatedItem = "nonMatchingItem")
        whenever(cassandraAmendmentRepository.findBy(any())).thenReturn(listOf(amendment))
        val node = getNode(AMENDMENT_REQUEST_JSON)
        val actualApiResponse = getAmendmentIdsHandler.handle(node)

        val expectedStatus = ResponseStatus.SUCCESS
        val expectedId = UUID.fromString(REQUEST_ID)
        val expectedVersion = ApiVersion.valueOf(REQUEST_VERSION)

        assertThat(actualApiResponse.result as List<*>, `is`((empty())))
        assertEquals(expectedStatus, actualApiResponse.status)
        assertEquals(expectedId, actualApiResponse.id)
        assertEquals(expectedVersion, actualApiResponse.version)
    }

    @Test
    fun handleNoRelatedItems() {
        val amendmentFirst = getTestAmendment().copy(
            id = UUID.randomUUID()
        )
        val amendmentSecond = getTestAmendment().copy(
            id = UUID.randomUUID()
        )
        whenever(cassandraAmendmentRepository.findBy(any())).thenReturn(listOf(amendmentFirst, amendmentSecond))

        val node = getNode(AMENDMENT_REQUEST_NO_RELATED_ITEMS_JSON)
        val actualApiResponse = getAmendmentIdsHandler.handle(node)
        val actualAmendmentIds = (actualApiResponse.result as List<String>).sorted()

        val expectedAmendmentIds = listOf(amendmentFirst.id, amendmentSecond.id).sorted()
        val expectedStatus = ResponseStatus.SUCCESS
        val expectedId = UUID.fromString(REQUEST_ID)
        val expectedVersion = ApiVersion.valueOf(REQUEST_VERSION)

        assertEquals(expectedAmendmentIds, actualAmendmentIds)
        assertEquals(expectedStatus, actualApiResponse.status)
        assertEquals(expectedId, actualApiResponse.id)
        assertEquals(expectedVersion, actualApiResponse.version)
    }

    @Test
    fun handleNonMatchingType() {

        val amendment = getTestAmendment().copy(
            type = AmendmentType.TENDER_CHANGE
        )
        whenever(cassandraAmendmentRepository.findBy(any())).thenReturn(listOf(amendment))
        val node = getNode(AMENDMENT_REQUEST_JSON)
        val actualApiResponse = getAmendmentIdsHandler.handle(node)

        val expectedStatus = ResponseStatus.SUCCESS
        val expectedId = UUID.fromString(REQUEST_ID)
        val expectedVersion = ApiVersion.valueOf(REQUEST_VERSION)

        assertThat(actualApiResponse.result as List<*>, `is`((empty())))
        assertEquals(expectedStatus, actualApiResponse.status)
        assertEquals(expectedId, actualApiResponse.id)
        assertEquals(expectedVersion, actualApiResponse.version)
    }

    @Test
    fun handleNoType() {

        val amendmentFirst = getTestAmendment().copy(
            id = UUID.randomUUID(),
            type = AmendmentType.TENDER_CHANGE
        )
        val amendmentSecond = getTestAmendment().copy(
            type = AmendmentType.CANCELLATION
        )
        whenever(cassandraAmendmentRepository.findBy(any())).thenReturn(listOf(amendmentFirst, amendmentSecond))

        val node = getNode(AMENDMENT_REQUEST_NO_TYPE_JSON)
        val actualApiResponse = getAmendmentIdsHandler.handle(node)
        val actualAmendmentIds = (actualApiResponse.result as List<String>).sorted()

        val expectedAmendmentIds = listOf(amendmentFirst.id, amendmentSecond.id).sorted()
        val expectedStatus = ResponseStatus.SUCCESS
        val expectedId = UUID.fromString(REQUEST_ID)
        val expectedVersion = ApiVersion.valueOf(REQUEST_VERSION)

        assertEquals(expectedAmendmentIds, actualAmendmentIds)
        assertEquals(expectedStatus, actualApiResponse.status)
        assertEquals(expectedId, actualApiResponse.id)
        assertEquals(expectedVersion, actualApiResponse.version)
    }

    @Test
    fun handleNoParamsReceived() {

        val amendmentFirst = getTestAmendment()
        val amendmentSecond = getTestAmendment().copy(
            status = AmendmentStatus.WITHDRAWN,
            relatedItem = "someItem",
            type = AmendmentType.TENDER_CHANGE,
            relatesTo = AmendmentRelatesTo.CAN
        )
        whenever(cassandraAmendmentRepository.findBy(any())).thenReturn(listOf(amendmentFirst, amendmentSecond))

        val node = getNode(AMENDMENT_REQUEST_NO_OPTIONAL_PARAMS_JSON)
        val actualApiResponse = getAmendmentIdsHandler.handle(node)

        val expectedAmendmentIds = listOf(amendmentFirst.id, amendmentSecond.id).sorted()
        val actualAmendmentIds = (actualApiResponse.result as List<String>).sorted()
        val expectedStatus = ResponseStatus.SUCCESS
        val expectedId = UUID.fromString(REQUEST_ID)
        val expectedVersion = ApiVersion.valueOf(REQUEST_VERSION)

        assertEquals(expectedAmendmentIds, actualAmendmentIds)
        assertEquals(expectedStatus, actualApiResponse.status)
        assertEquals(expectedId, actualApiResponse.id)
        assertEquals(expectedVersion, actualApiResponse.version)

    }


    @Test
    fun handleDuplicateRelatedItems() {

        val amendment = getTestAmendment()

        whenever(cassandraAmendmentRepository.findBy(any())).thenReturn(listOf(amendment))

        val node = getNode(AMENDMENT_REQUEST_DUPLICATE_ITEMS_JSON)
        val actualApiResponse = getAmendmentIdsHandler.handle(node)
        val actualAmendmentIds = (actualApiResponse.result as List<String>).sorted()

        val expectedAmendmentIds = listOf(amendment.id)
        val expectedStatus = ResponseStatus.SUCCESS
        val expectedId = UUID.fromString(REQUEST_ID)
        val expectedVersion = ApiVersion.valueOf(REQUEST_VERSION)

        assertEquals(expectedAmendmentIds, actualAmendmentIds)
        assertEquals(expectedStatus, actualApiResponse.status)
        assertEquals(expectedId, actualApiResponse.id)
        assertEquals(expectedVersion, actualApiResponse.version)
    }
}