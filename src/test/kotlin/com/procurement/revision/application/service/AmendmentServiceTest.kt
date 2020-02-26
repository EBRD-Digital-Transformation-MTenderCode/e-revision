package com.procurement.revision.application.service

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.procurement.revision.application.model.amendment.CreateAmendmentParams
import com.procurement.revision.application.model.amendment.CreateAmendmentResult
import com.procurement.revision.application.model.amendment.DataValidationParams
import com.procurement.revision.application.model.amendment.GetAmendmentIdsParams
import com.procurement.revision.application.repository.AmendmentRepository
import com.procurement.revision.domain.enums.AmendmentRelatesTo
import com.procurement.revision.domain.enums.AmendmentStatus
import com.procurement.revision.domain.enums.AmendmentType
import com.procurement.revision.domain.enums.DocumentType
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
            whenever(amendmentRepository.findBy(any(), any())).thenReturn(listOf(amendmentFirst, amendmentSecond))

            val actualIds = amendmentService.getAmendmentIdsBy(
                GetAmendmentIdsParams(
                    status = amendmentFirst.status,
                    relatesTo = amendmentFirst.relatesTo,
                    relatedItems = listOf(amendmentFirst.relatedItem),
                    cpid = "cpid",
                    ocid = "ocid",
                    type = amendmentFirst.type
                )
            ).get
            val expectedIds = listOf(amendmentFirst.id)

            assertEquals(expectedIds, actualIds)
        }

        @Test
        fun handleNonMatchingStatuses() {
            val amendment = getTestAmendment()
            whenever(amendmentRepository.findBy(any(), any())).thenReturn(listOf(amendment))

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
            assertTrue(actualIds.get.isEmpty())
        }

        @Test
        fun handleNoStatus() {
            val amendmentFirst = getTestAmendment()
            val amendmentSecond = getTestAmendment().copy(status = AmendmentStatus.ACTIVE)
            whenever(amendmentRepository.findBy(any(), any())).thenReturn(listOf(amendmentFirst, amendmentSecond))

            val actualIds = amendmentService.getAmendmentIdsBy(
                GetAmendmentIdsParams(
                    status = null,
                    relatesTo = amendmentFirst.relatesTo,
                    relatedItems = listOf(amendmentFirst.relatedItem, amendmentSecond.relatedItem),
                    cpid = "cpid",
                    ocid = "ocid",
                    type = amendmentFirst.type
                )
            ).get.sorted()

            val expectedIds = listOf(amendmentFirst.id, amendmentSecond.id).sorted()

            assertEquals(expectedIds, actualIds)
        }

        @Test
        fun handleNonMatchingRelatesTo() {
            val amendment = getTestAmendment()
            whenever(amendmentRepository.findBy(any(), any())).thenReturn(listOf(amendment))

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
            assertTrue(actualIds.get.isEmpty())
        }

        @Test
        fun handleNoRelatesTo() {
            val amendmentFirst = getTestAmendment()
            val amendmentSecond = getTestAmendment().copy(relatesTo = AmendmentRelatesTo.CAN)
            whenever(amendmentRepository.findBy(any(), any())).thenReturn(listOf(amendmentFirst, amendmentSecond))

            val actualIds = amendmentService.getAmendmentIdsBy(
                GetAmendmentIdsParams(
                    status = amendmentFirst.status,
                    relatesTo = null,
                    relatedItems = listOf(amendmentFirst.relatedItem, amendmentSecond.relatedItem),
                    cpid = "cpid",
                    ocid = "ocid",
                    type = amendmentFirst.type
                )
            ).get.sorted()

            val expectedIds = listOf(amendmentFirst.id, amendmentSecond.id).sorted()

            assertEquals(expectedIds, actualIds)
        }

        @Test
        fun handleNonMatchingRelatedItems() {
            val amendment = getTestAmendment()
            whenever(amendmentRepository.findBy(any(), any())).thenReturn(listOf(amendment))

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
            assertTrue(actualIds.get.isEmpty())
        }

        @Test
        fun handleNoRelatedItems() {
            val amendmentFirst = getTestAmendment()
            val amendmentSecond = getTestAmendment()
            whenever(amendmentRepository.findBy(any(), any())).thenReturn(listOf(amendmentFirst, amendmentSecond))

            val actualIds = amendmentService.getAmendmentIdsBy(
                GetAmendmentIdsParams(
                    status = amendmentFirst.status,
                    relatesTo = null,
                    relatedItems = emptyList(),
                    cpid = "cpid",
                    ocid = "ocid",
                    type = amendmentFirst.type
                )
            ).get.sorted()

            val expectedIds = listOf(amendmentFirst.id, amendmentSecond.id).sorted()

            assertEquals(expectedIds, actualIds)
        }

        @Test
        fun handleNonMatchingType() {
            val amendment = getTestAmendment()
            whenever(amendmentRepository.findBy(any(), any())).thenReturn(listOf(amendment))

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
            assertTrue(actualIds.get.isEmpty())
        }

        @Test
        fun handleNoType() {
            val amendmentFirst = getTestAmendment()
            val amendmentSecond = getTestAmendment().copy(type = AmendmentType.TENDER_CHANGE)
            whenever(amendmentRepository.findBy(any(), any())).thenReturn(listOf(amendmentFirst, amendmentSecond))

            val actualIds = amendmentService.getAmendmentIdsBy(
                GetAmendmentIdsParams(
                    status = amendmentFirst.status,
                    relatesTo = null,
                    relatedItems = listOf(amendmentFirst.relatedItem, amendmentSecond.relatedItem),
                    cpid = "cpid",
                    ocid = "ocid",
                    type = null
                )
            ).get.sorted()

            val expectedIds = listOf(amendmentFirst.id, amendmentSecond.id).sorted()

            assertEquals(expectedIds, actualIds)
        }

        @Test
        fun handleNoParamsReceived() {
            val amendment = getTestAmendment()
            whenever(amendmentRepository.findBy(any(), any())).thenReturn(listOf(amendment))

            val actualIds = amendmentService.getAmendmentIdsBy(
                GetAmendmentIdsParams(
                    status = null,
                    relatesTo = null,
                    relatedItems = emptyList(),
                    cpid = "cpid",
                    ocid = "ocid",
                    type = null
                )
            ).get
            val expectedIds = listOf(amendment.id)

            assertEquals(expectedIds, actualIds)
        }

        @Test
        fun handleDuplicateRelatedItems() {
            val amendment = getTestAmendment()
            val amendmentsInDb = listOf(amendment)
            whenever(amendmentRepository.findBy(any(), any())).thenReturn(amendmentsInDb)

            val actualIds = amendmentService.getAmendmentIdsBy(
                GetAmendmentIdsParams(
                    status = amendment.status,
                    relatesTo = amendment.relatesTo,
                    relatedItems = listOf(amendment.relatedItem, amendment.relatedItem),
                    cpid = "cpid",
                    ocid = "ocid",
                    type = amendment.type
                )
            ).get.sorted()

            val expectedIds = amendmentsInDb.map { it.id }.sorted()

            assertEquals(expectedIds, actualIds)
        }
    }

    @Nested
    inner class ValidateDocumentsTypes {

        private fun getDocument(documentType: DocumentType) = DataValidationParams.Amendment.Document(
            documentType = documentType,
            description = "documentDescription",
            title = "title",
            id = UUID.randomUUID().toString()
        )

        private fun getAmendment(documents: List<DataValidationParams.Amendment.Document>) = DataValidationParams.Amendment(
            description = "description",
            rationale = "rationale",
            documents = documents
        )

        private fun getFullData(
            amendments: List<DataValidationParams.Amendment>,
            operationType: OperationType
        ) = DataValidationParams(
            cpid = "cpid",
            ocid = "ocid",
            operationType = operationType,
            amendments = amendments
        )

        @Test
        fun success() {
            val matchingDocTypesByOperationType = mapOf(
                OperationType.TENDER_CANCELLATION to setOf(DocumentType.CANCELLATION_DETAILS),
                OperationType.LOT_CANCELLATION to setOf(DocumentType.CANCELLATION_DETAILS)
            )
            matchingDocTypesByOperationType.forEach { operationType, matchingDocTypes ->
                matchingDocTypes.forEach { matchingDocType ->
                    val docWithMatchingType = getDocument(matchingDocType)
                    val amendment = getAmendment(listOf(docWithMatchingType))
                    val params = getFullData(
                        amendments = listOf(amendment),
                        operationType = operationType
                    )
                    assertTrue(amendmentService.validateDocumentsTypes(params).isOk)
                }
            }
        }

        @Test
        fun nonMatchingDocAndOperationTypes_exception() {

            val nonMatchingDocTypesByOperationType = mapOf(
                OperationType.TENDER_CANCELLATION to DocumentType.values().subtract(setOf(DocumentType.CANCELLATION_DETAILS)),
                OperationType.LOT_CANCELLATION to DocumentType.values().subtract(setOf(DocumentType.CANCELLATION_DETAILS))
            )
            nonMatchingDocTypesByOperationType.forEach { (operationType, nonMatchingDocTypes) ->
                nonMatchingDocTypes.forEach { nonMatchingDocType ->
                    val docWithNonMatchingType = getDocument(nonMatchingDocType)
                    val amendment = getAmendment(listOf(docWithNonMatchingType))
                    val params = getFullData(
                        amendments = listOf(amendment),
                        operationType = operationType
                    )
                    assertTrue(amendmentService.validateDocumentsTypes(params).isError)
                }
            }
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
            whenever(amendmentRepository.saveNewAmendment(cpid = eq(params.cpid), ocid = eq(params.ocid), amendment = any())).thenReturn(true)
            val actual = amendmentService.createAmendment(params).get

            assertEquals(expected, actual)
        }

        @Test
        fun successForLotCancellation() {
            val params = createAmendmentParams().copy(operationType = OperationType.LOT_CANCELLATION)

            val token = UUID.randomUUID()

            val expected = createAmendmentResult(
                params,
                token
            ).run { copy(amendment = this.amendment.copy(relatesTo = AmendmentRelatesTo.LOT)) }

            whenever(generable.generateToken()).thenReturn(token)
            whenever(amendmentRepository.saveNewAmendment(cpid = eq(params.cpid), ocid = eq(params.ocid), amendment = any())).thenReturn(true)
            val actual = amendmentService.createAmendment(params).get

            assertEquals(expected, actual)
        }

        private fun createAmendmentResult(params: CreateAmendmentParams, token: UUID) =
            CreateAmendmentResult(
                amendment = params.amendment.let { amendment ->
                    CreateAmendmentResult.Amendment(
                        rationale = amendment.rationale,
                        description = amendment.description,
                        id = amendment.id,
                        relatedItem = params.id.toString(),
                        relatesTo = AmendmentRelatesTo.TENDER,
                        status = AmendmentStatus.PENDING,
                        type = AmendmentType.CANCELLATION,
                        token = token,
                        date = params.startDate,
                        documents = amendment.documents.map { document ->
                            CreateAmendmentResult.Amendment.Document(
                                id = document.id,
                                description = document.description,
                                title = document.title,
                                documentType = document.documentType
                            )
                        }
                    )
                }
            )

        @Test
        fun successGetAmendmentFromHistory() {
            val params = createAmendmentParams().copy(operationType = OperationType.LOT_CANCELLATION)

            val token = UUID.randomUUID()

            whenever(generable.generateToken()).thenReturn(token)
            whenever(amendmentRepository.saveNewAmendment(cpid = eq(params.cpid), ocid = eq(params.ocid), amendment = any())).thenReturn(false)

            val amendmentFromDb = getTestAmendment()
            whenever(amendmentRepository.findBy(params.cpid, params.ocid, params.amendment.id)).thenReturn(amendmentFromDb)

            val expected = CreateAmendmentResult(
                amendment = CreateAmendmentResult.Amendment(
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
                        CreateAmendmentResult.Amendment.Document(
                            id = document.id,
                            description = document.description,
                            documentType = document.documentType,
                            title = document.title
                        )
                    }
                )
            )

            val actual = amendmentService.createAmendment(params).get

            assertEquals(expected, actual)
        }

        private fun createAmendmentParams(): CreateAmendmentParams {
            return CreateAmendmentParams(
                id = UUID.randomUUID(),
                cpid = "cpid",
                ocid = "ocid",
                operationType = OperationType.TENDER_CANCELLATION,
                owner = "owner",
                startDate = LocalDateTime.now(),
                amendment = CreateAmendmentParams.Amendment(
                    rationale = "rationale",
                    description = "description",
                    id = UUID.randomUUID(),
                    documents = listOf(
                        CreateAmendmentParams.Amendment.Document(
                            id = "documentId",
                            description = "description",
                            documentType = DocumentType.CANCELLATION_DETAILS,
                            title = "title"
                        )
                    )
                )
            )
        }
    }
}