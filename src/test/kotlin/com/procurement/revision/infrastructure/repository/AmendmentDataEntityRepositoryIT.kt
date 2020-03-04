package com.procurement.revision.infrastructure.repository

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.Cluster
import com.datastax.driver.core.HostDistance
import com.datastax.driver.core.PlainTextAuthProvider
import com.datastax.driver.core.PoolingOptions
import com.datastax.driver.core.Session
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doThrow
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import com.procurement.revision.application.repository.AmendmentRepository
import com.procurement.revision.domain.enums.AmendmentRelatesTo
import com.procurement.revision.domain.enums.AmendmentStatus
import com.procurement.revision.domain.enums.AmendmentType
import com.procurement.revision.domain.enums.DocumentType
import com.procurement.revision.domain.model.amendment.Amendment
import com.procurement.revision.infrastructure.bind.databinding.JsonDateTimeDeserializer
import com.procurement.revision.infrastructure.bind.databinding.JsonDateTimeSerializer
import com.procurement.revision.infrastructure.configuration.DatabaseTestConfiguration
import com.procurement.revision.infrastructure.exception.DatabaseInteractionException
import com.procurement.revision.infrastructure.model.entity.AmendmentDataEntity
import com.procurement.revision.json.toJson
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.collection.IsEmptyCollection.empty
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime
import java.util.*

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [DatabaseTestConfiguration::class])
class AmendmentDataEntityRepositoryIT {
    companion object {
        private const val CPID = "cpid-1"
        private const val OCID = "ocid-1"
        private val ID = UUID.randomUUID()
        private val TOKEN = UUID.randomUUID()
        private val DATE = JsonDateTimeDeserializer.deserialize(JsonDateTimeSerializer.serialize(LocalDateTime.now()))
        private const val JSON_DATA = """ {"ac": "data"} """

        private const val KEYSPACE = "revision"
        private const val TABLE_NAME = "amendments"
        private const val COLUMN_CPID = "cpid"
        private const val COLUMN_ID = "id"
        private const val COLUMN_DATA = "data"
    }

    @Autowired
    private lateinit var container: CassandraTestContainer

    private lateinit var session: Session
    private lateinit var amendmentRepository: AmendmentRepository

    @BeforeEach
    fun init() {
        val poolingOptions = PoolingOptions()
            .setMaxConnectionsPerHost(HostDistance.LOCAL, 1)
        val cluster = Cluster.builder()
            .addContactPoints(container.contractPoint)
            .withPort(container.port)
            .withoutJMXReporting()
            .withPoolingOptions(poolingOptions)
            .withAuthProvider(PlainTextAuthProvider(container.username, container.password))
            .build()

        session = spy(cluster.connect())

        createKeyspace()
        createTable()

        amendmentRepository = CassandraAmendmentRepository(session)
    }

    @AfterEach
    fun clean() {
        dropKeyspace()
    }

    @Test
    fun findBy() {
        insertAmendment()

        val actualAmendments: List<Amendment> = amendmentRepository.findBy(cpid = CPID, ocid = OCID).get

        assertThat(actualAmendments, `is`(not(empty<Amendment>())))
        assertThat(actualAmendments, hasItem(stubAmendment()))
//        assertEquals(listOf(stubAmendment()), actualAmendments)
    }

    @Test
    fun cnNotFound() {
        val actualAmendments = amendmentRepository.findBy(cpid = "UNKNOWN", ocid = OCID).get
        assertThat(actualAmendments, `is`(empty<Amendment>()))
    }

    @Test
    fun `error while saving`() {
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        assertThrows<DatabaseInteractionException> {
            amendmentRepository.saveNewAmendment(CPID, OCID, stubAmendment())
        }
    }

    @Test
    fun `error while finding`() {
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        assertThrows<DatabaseInteractionException> { amendmentRepository.findBy(CPID, OCID) }
    }

    private fun createKeyspace() {
        session.execute("CREATE KEYSPACE $KEYSPACE " +
                            "WITH replication = {'class' : 'SimpleStrategy', 'replication_factor' : 1};")
    }

    private fun dropKeyspace() {
        session.execute("DROP KEYSPACE $KEYSPACE;")
    }

    private fun createTable() {
        session.execute(
            """
                CREATE TABLE IF NOT EXISTS revision.amendments
                    (
                        cpid      text,
                        id        uuid,
                        data      text,
                        primary key (cpid, id)
                    );
            """
        )
    }

    private fun insertAmendment(
        cpid: String = CPID,
        id: UUID = ID
    ) {

        val entity = convert(stubAmendment())
        val record = QueryBuilder.insertInto(KEYSPACE, TABLE_NAME)
            .value(COLUMN_CPID, cpid)
            .value(COLUMN_ID, id)
            .value(COLUMN_DATA, entity.toJson())
        session.execute(record)
    }

    private fun stubAmendment() = Amendment(
        token = TOKEN,
        owner = "amendment.owner",
        id = ID,
        description = "amendment.description",
        rationale = "amendment.rationale",
        relatesTo = AmendmentRelatesTo.TENDER,
        relatedItem = "amendment.relatedItem",
        status = AmendmentStatus.PENDING,
        type = AmendmentType.CANCELLATION,
        date = DATE,
        documents = listOf(
            Amendment.Document(
                id = "document.id",
                documentType = DocumentType.BIDDING_DOCUMENTS,
                description = "document.description",
                title = "document.title"
            )
        )

    )

    fun convert(amendment: Amendment) = AmendmentDataEntity(
        token = amendment.token,
        owner = amendment.owner,
        amendment = AmendmentDataEntity.AmendmentEntity(
            id = amendment.id,
            description = amendment.description,
            rationale = amendment.rationale,
            relatesTo = amendment.relatesTo,
            relatedItem = amendment.relatedItem,
            status = amendment.status,
            type = amendment.type,
            date = amendment.date,
            documents = amendment.documents
                .map { document ->
                    AmendmentDataEntity.AmendmentEntity.Document(
                        id = document.id,
                        documentType = document.documentType,
                        description = document.description,
                        title = document.title
                    )
                }
        )
    )
}
