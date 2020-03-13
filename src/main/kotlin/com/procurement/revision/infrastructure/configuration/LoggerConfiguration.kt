package com.procurement.revision.infrastructure.configuration

import com.procurement.revision.application.service.Logger
import com.procurement.revision.infrastructure.service.CustomLogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LoggerConfiguration {

    @Bean
    fun logger(): Logger = CustomLogger()
}