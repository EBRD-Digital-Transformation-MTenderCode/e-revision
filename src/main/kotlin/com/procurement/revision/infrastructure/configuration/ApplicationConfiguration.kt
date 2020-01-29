package com.procurement.revision.infrastructure.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    WebConfiguration::class,
    DatabaseConfiguration::class,
    ServiceConfiguration::class,
    ObjectMapperConfiguration::class
)
class ApplicationConfiguration
