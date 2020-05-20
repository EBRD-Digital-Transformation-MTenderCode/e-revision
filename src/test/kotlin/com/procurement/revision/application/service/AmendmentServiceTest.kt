package com.procurement.revision.application.service

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.procurement.revision.application.model.amendment.CreateAmendmentParams
import com.procurement.revision.application.model.amendment.CreateAmendmentResult
import com.procurement.revision.application.model.amendment.FindAmendmentIdsParams
import com.procurement.revision.application.repository.AmendmentRepository
import com.procurement.revision.domain.enums.AmendmentRelatesTo
import com.procurement.revision.domain.enums.AmendmentStatus
import com.procurement.revision.domain.enums.AmendmentType
import com.procurement.revision.domain.enums.DocumentType
import com.procurement.revision.domain.functional.Option
import com.procurement.revision.domain.functional.Result
import com.procurement.revision.domain.model.amendment.Amendment
import com.procurement.revision.infrastructure.model.OperationType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

internal class AmendmentServiceTest {

    companion object {
        private const val CPID = "ocds-t1s2t3-MD-1580306096763"
        private const val OCID = "ocds-t1s2t3-MD-1580306096762-EV-1582034422825"
    }

    private lateinit var amendmentRepository: AmendmentRepository
    private lateinit var amendmentService: AmendmentService
    private lateinit var generable: Generable

    @BeforeEach
    fun init() {
        amendmentRepository = mock()
        generable = mock()
        amendmentService = AmendmentService(amendmentRepository, generable)
    }

    private fun getTestAmendment() = Amendment(
        id = UUID.randomUUID(),
        token = UUID.randomUUID(),
        owner = UUID.randomUUID(),
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
    inner class FindAmendmentIdsBy {

        @Test
        fun findAmendmentIdsBySuccess() {
            val amendmentFirst = getTestAmendment()
            val amendmentSecond = getTestAmendment().copy(relatedItem = "someItem")
            whenever(amendmentRepository.findBy(any(), any())).thenReturn(
                Result.success(
                    listOf(
                        amendmentFirst,
                        amendmentSecond
                    )
                )
            )

            val actualIds = amendmentService.findAmendmentIdsBy(
                FindAmendmentIdsParams.tryCreate(
                    status = amendmentFirst.status.toString(),
                    relatesTo = amendmentFirst.relatesTo.toString(),
                    relatedItems = listOf(amendmentFirst.relatedItem),
                    cpid = CPID,
                    ocid = OCID,
                    type = amendmentFirst.type.toString()
                ).get
            ).get
            val expectedIds = listOf(amendmentFirst.id)

            assertEquals(expectedIds, actualIds)
        }

        @Test
        fun handleNoStatus() {
            val amendmentFirst = getTestAmendment()
            val amendmentSecond = getTestAmendment().copy(status = AmendmentStatus.ACTIVE)
            whenever(amendmentRepository.findBy(any(), any())).thenReturn(
                Result.success(
                    listOf(
                        amendmentFirst,
                        amendmentSecond
                    )
                )
            )

            val actualIds = amendmentService.findAmendmentIdsBy(
                FindAmendmentIdsParams.tryCreate(
                    status = null,
                    relatesTo = amendmentFirst.relatesTo.toString(),
                    relatedItems = listOf(amendmentFirst.relatedItem, amendmentSecond.relatedItem),
                    cpid = CPID,
                    ocid = OCID,
                    type = amendmentFirst.type.toString()
                ).get
            ).get.sorted()

            val expectedIds = listOf(amendmentFirst.id, amendmentSecond.id).sorted()

            assertEquals(expectedIds, actualIds)
        }

        @Test
        fun handleNonMatchingRelatesTo() {
            val amendment = getTestAmendment()
            whenever(amendmentRepository.findBy(any(), any())).thenReturn(Result.success(listOf(amendment)))

            val nonMatchingRelatesTo = AmendmentRelatesTo.TENDER
            val actualIds = amendmentService.findAmendmentIdsBy(
                FindAmendmentIdsParams.tryCreate(
                    status = amendment.status.toString(),
                    relatesTo = nonMatchingRelatesTo.toString(),
                    relatedItems = listOf(amendment.relatedItem),
                    cpid = CPID,
                    ocid = OCID,
                    type = amendment.type.toString()
                ).get
            )
            assertTrue(actualIds.get.isEmpty())
        }

        @Test
        fun handleNoRelatesTo() {
            val amendmentFirst = getTestAmendment()
            val amendmentSecond = getTestAmendment().copy(relatesTo = AmendmentRelatesTo.CAN)
            whenever(amendmentRepository.findBy(any(), any())).thenReturn(
                Result.success(
                    listOf(
                        amendmentFirst,
                        amendmentSecond
                    )
                )
            )

            val actualIds = amendmentService.findAmendmentIdsBy(
                FindAmendmentIdsParams.tryCreate(
                    status = amendmentFirst.status.toString(),
                    relatesTo = null,
                    relatedItems = listOf(amendmentFirst.relatedItem, amendmentSecond.relatedItem),
                    cpid = CPID,
                    ocid = OCID,
                    type = amendmentFirst.type.toString()
                ).get
            ).get.sorted()

            val expectedIds = listOf(amendmentFirst.id, amendmentSecond.id).sorted()

            assertEquals(expectedIds, actualIds)
        }

        @Test
        fun handleNonMatchingRelatedItems() {
            val amendment = getTestAmendment()
            whenever(amendmentRepository.findBy(any(), any())).thenReturn(Result.success(listOf(amendment)))

            val nonMatchingRelatedItems = listOf("someItem")
            val actualIds = amendmentService.findAmendmentIdsBy(
                FindAmendmentIdsParams.tryCreate(
                    status = amendment.status.toString(),
                    relatesTo = amendment.relatesTo.toString(),
                    relatedItems = nonMatchingRelatedItems,
                    cpid = CPID,
                    ocid = OCID,
                    type = amendment.type.toString()
                ).get
            )
            assertTrue(actualIds.get.isEmpty())
        }

        @Test
        fun handleNoRelatedItems() {
            val amendmentFirst = getTestAmendment()
            val amendmentSecond = getTestAmendment()
            whenever(amendmentRepository.findBy(any(), any())).thenReturn(
                Result.success(
                    listOf(
                        amendmentFirst,
                        amendmentSecond
                    )
                )
            )

            val actualIds = amendmentService.findAmendmentIdsBy(
                FindAmendmentIdsParams.tryCreate(
                    status = amendmentFirst.status.toString(),
                    relatesTo = null,
                    relatedItems = null,
                    cpid = CPID,
                    ocid = OCID,
                    type = amendmentFirst.type.toString()
                ).get
            ).get.sorted()

            val expectedIds = listOf(amendmentFirst.id, amendmentSecond.id).sorted()

            assertEquals(expectedIds, actualIds)
        }

        @Test
        fun handleNoType() {
            val amendmentFirst = getTestAmendment()
            val amendmentSecond = getTestAmendment().copy(type = AmendmentType.TENDER_CHANGE)
            whenever(amendmentRepository.findBy(any(), any())).thenReturn(
                Result.success(
                    listOf(
                        amendmentFirst,
                        amendmentSecond
                    )
                )
            )

            val actualIds = amendmentService.findAmendmentIdsBy(
                FindAmendmentIdsParams.tryCreate(
                    status = amendmentFirst.status.toString(),
                    relatesTo = null,
                    relatedItems = listOf(amendmentFirst.relatedItem, amendmentSecond.relatedItem),
                    cpid = CPID,
                    ocid = OCID,
                    type = null
                ).get
            ).get.sorted()

            val expectedIds = listOf(amendmentFirst.id, amendmentSecond.id).sorted()

            assertEquals(expectedIds, actualIds)
        }

        @Test
        fun handleNoParamsReceived() {
            val amendment = getTestAmendment()
            whenever(amendmentRepository.findBy(any(), any())).thenReturn(Result.success(listOf(amendment)))

            val actualIds = amendmentService.findAmendmentIdsBy(
                FindAmendmentIdsParams.tryCreate(
                    status = null,
                    relatesTo = null,
                    relatedItems = null,
                    cpid = CPID,
                    ocid = OCID,
                    type = null
                ).get
            ).get
            val expectedIds = listOf(amendment.id)

            assertEquals(expectedIds, actualIds)
        }

        @Test
        fun handleDuplicateRelatedItems() {
            val amendment = getTestAmendment()
            val amendmentsInDb = listOf(amendment)
            whenever(amendmentRepository.findBy(any(), any())).thenReturn(Result.success(amendmentsInDb))

            val actualIds = amendmentService.findAmendmentIdsBy(
                FindAmendmentIdsParams.tryCreate(
                    status = amendment.status.toString(),
                    relatesTo = amendment.relatesTo.toString(),
                    relatedItems = listOf(amendment.relatedItem, amendment.relatedItem),
                    cpid = CPID,
                    ocid = OCID,
                    type = amendment.type.toString()
                ).get
            ).get.sorted()

            val expectedIds = amendmentsInDb.map { it.id }.sorted()

            assertEquals(expectedIds, actualIds)
        }
    }

    @Nested
    inner class CreateAmendment {
        @Test
        fun successForTenderCancellation() {
            val params = createAmendmentParams()

            val token = UUID.randomUUID()

            val expected = createAmendmentResult(params, token)

            whenever(generable.generateToken()).thenReturn(token)
            whenever(
                amendmentRepository.saveNewAmendment(
                    cpid = eq(params.cpid),
                    ocid = eq(params.ocid),
                    amendment = any()
                )
            ).thenReturn(Result.success(true))
            val actual = amendmentService.createAmendment(params).get

            assertEquals(expected, actual)
        }

        @Test
        fun successForLotCancellation() {
            val params = createAmendmentParams(operationType = OperationType.LOT_CANCELLATION)

            val token = UUID.randomUUID()

            val expected = createAmendmentResult(
                params,
                token
            ).run { this.copy(relatesTo = AmendmentRelatesTo.LOT) }

            whenever(generable.generateToken()).thenReturn(token)
            whenever(
                amendmentRepository.saveNewAmendment(
                    cpid = eq(params.cpid),
                    ocid = eq(params.ocid),
                    amendment = any()
                )
            ).thenReturn(Result.success(true))
            val actual = amendmentService.createAmendment(params).get

            assertEquals(expected, actual)
        }

        private fun createAmendmentResult(params: CreateAmendmentParams, token: UUID) =
            params.amendment.let { amendment ->
                CreateAmendmentResult(
                    rationale = amendment.rationale,
                    description = amendment.description,
                    id = amendment.id,
                    relatedItem = params.relatedEntityId,
                    relatesTo = AmendmentRelatesTo.TENDER,
                    status = AmendmentStatus.PENDING,
                    type = AmendmentType.CANCELLATION,
                    token = token,
                    date = params.date,
                    documents = amendment.documents.map { document ->
                        CreateAmendmentResult.Document(
                            id = document.id,
                            description = document.description,
                            title = document.title,
                            documentType = document.documentType
                        )
                    }
                )
            }

        @Test
        fun successGetAmendmentFromHistory() {
            val params = createAmendmentParams(operationType = OperationType.LOT_CANCELLATION)

            val token = UUID.randomUUID()

            whenever(generable.generateToken()).thenReturn(token)
            whenever(
                amendmentRepository.saveNewAmendment(
                    cpid = eq(params.cpid),
                    ocid = eq(params.ocid),
                    amendment = any()
                )
            ).thenReturn(Result.success(false))

            val amendmentFromDb = getTestAmendment()
            whenever(amendmentRepository.findBy(params.cpid, params.ocid, params.amendment.id)).thenReturn(
                Result.success(amendmentFromDb)
            )

            val expected = CreateAmendmentResult(
                relatesTo = amendmentFromDb.relatesTo,
                description = amendmentFromDb.description,
                date = amendmentFromDb.date,
                token = amendmentFromDb.token,
                type = amendmentFromDb.type,
                status = amendmentFromDb.status,
                relatedItem = amendmentFromDb.relatedItem,
                id = amendmentFromDb.id,
                rationale = amendmentFromDb.rationale,
                documents = amendmentFromDb.documents.map { document ->
                    CreateAmendmentResult.Document(
                        id = document.id,
                        description = document.description,
                        documentType = document.documentType,
                        title = document.title
                    )
                }
            )

            val actual = amendmentService.createAmendment(params).get

            assertEquals(expected, actual)
        }

        private fun createAmendmentParams(operationType: OperationType = OperationType.TENDER_CANCELLATION): CreateAmendmentParams {
            return CreateAmendmentParams.tryCreate(
                relatedEntityId = UUID.randomUUID().toString(),
                cpid = "ocds-t1s2t3-MD-1580306096762",
                ocid = "ocds-t1s2t3-MD-1580306096762-EV-1582034422825",
                operationType = operationType.toString(),
                owner = UUID.randomUUID().toString(),
                date = "2019-10-04T15:51:23Z",
                amendment = CreateAmendmentParams.Amendment.tryCreate(
                    rationale = "rationale",
                    description = "description",
                    id = UUID.randomUUID().toString(),
                    documents = Option.pure(
                        listOf(
                            CreateAmendmentParams.Amendment.Document.tryCreate(
                                id = "documentId",
                                description = "description",
                                documentType = DocumentType.CANCELLATION_DETAILS.toString(),
                                title = "title"
                            ).get
                        )
                    )
                ).get
            ).get
        }
    }
}