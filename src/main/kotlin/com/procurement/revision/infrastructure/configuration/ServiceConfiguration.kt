package com.procurement.revision.infrastructure.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.procurement.revision.application.repository.AmendmentRepository

import com.procurement.revision.infrastructure.service.GenerationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(
    basePackages = [
        "com.procurement.revision.infrastructure.service"
    ]
)
class ServiceConfiguration {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var amendmentRepository: AmendmentRepository


    @Bean
    fun generationService(): GenerationService {
        return GenerationService()
    }
}
