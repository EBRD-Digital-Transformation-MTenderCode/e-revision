package com.procurement.revision.infrastructure.configuration

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = ["com.procurement.revision.infrastructure.web.controller"])
class WebConfiguration
