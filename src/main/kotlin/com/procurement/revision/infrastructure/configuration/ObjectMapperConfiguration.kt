package com.procurement.revision.infrastructure.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.procurement.revision.infrastructure.bind.jackson.configuration
import org.springframework.context.annotation.Configuration

@Configuration
class ObjectMapperConfiguration(objectMapper: ObjectMapper) {

    init {
        objectMapper.configuration()
    }
}
