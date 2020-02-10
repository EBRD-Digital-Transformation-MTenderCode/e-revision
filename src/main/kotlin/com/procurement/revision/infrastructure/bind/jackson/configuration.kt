package com.procurement.revision.infrastructure.bind.jackson

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.procurement.revision.domain.enums.AmendmentRelatesTo
import com.procurement.revision.domain.enums.AmendmentStatus
import com.procurement.revision.domain.enums.AmendmentType
import com.procurement.revision.domain.enums.DocumentType
import com.procurement.revision.domain.enums.MainProcurementCategory
import com.procurement.revision.domain.enums.ProcurementMethod
import com.procurement.revision.infrastructure.bind.apiversion.ApiVersionDeserializer
import com.procurement.revision.infrastructure.bind.apiversion.ApiVersionSerializer
import com.procurement.revision.infrastructure.bind.databinding.EnumDeserializer
import com.procurement.revision.infrastructure.bind.databinding.EnumSerializer
import com.procurement.revision.infrastructure.bind.databinding.JsonDateTimeDeserializer
import com.procurement.revision.infrastructure.bind.databinding.JsonDateTimeSerializer
import com.procurement.revision.infrastructure.model.OperationType
import com.procurement.revision.infrastructure.web.dto.ApiVersion
import java.time.LocalDateTime

fun ObjectMapper.configuration() {
    val module = SimpleModule().apply {
        /**
         * Serializer/Deserializer for LocalDateTime type
         */
        addSerializer(LocalDateTime::class.java, JsonDateTimeSerializer())
        addDeserializer(LocalDateTime::class.java, JsonDateTimeDeserializer())

        /**
         * Serializer/Deserializer for ApiVersion type
         */
        addSerializer(ApiVersion::class.java, ApiVersionSerializer())
        addDeserializer(ApiVersion::class.java, ApiVersionDeserializer())

        /**
         *  Serializer/Deserializer for enum type
         */
        addDeserializer(AmendmentStatus::class.java, EnumDeserializer(AmendmentStatus))
        addSerializer(AmendmentStatus::class.java, EnumSerializer<AmendmentStatus>())

        addDeserializer(AmendmentType::class.java, EnumDeserializer(AmendmentType))
        addSerializer(AmendmentType::class.java, EnumSerializer<AmendmentType>())

        addDeserializer(DocumentType::class.java, EnumDeserializer(DocumentType))
        addSerializer(DocumentType::class.java, EnumSerializer<DocumentType>())

        addDeserializer(AmendmentRelatesTo::class.java, EnumDeserializer(AmendmentRelatesTo))
        addSerializer(AmendmentRelatesTo::class.java, EnumSerializer<AmendmentRelatesTo>())

        addDeserializer(OperationType::class.java, EnumDeserializer(OperationType))
        addSerializer(OperationType::class.java, EnumSerializer<OperationType>())

        addDeserializer(ProcurementMethod::class.java, EnumDeserializer(ProcurementMethod))
        addSerializer(ProcurementMethod::class.java, EnumSerializer<ProcurementMethod>())

        addDeserializer(MainProcurementCategory::class.java, EnumDeserializer(MainProcurementCategory))
        addSerializer(MainProcurementCategory::class.java, EnumSerializer<MainProcurementCategory>())

    }

    this.registerModule(module)
    this.registerModule(KotlinModule())
    this.configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true)
    this.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
    this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    this.nodeFactory = JsonNodeFactory.withExactBigDecimals(true)
}
