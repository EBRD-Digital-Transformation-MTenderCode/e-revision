package com.procurement.revision

import com.procurement.revision.infrastructure.configuration.ApplicationConfiguration
import com.procurement.revision.infrastructure.configuration.properties.GlobalProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackageClasses = [ApplicationConfiguration::class])
class RevisionApplication

fun main(args: Array<String>) {
    runApplication<RevisionApplication>(*args)
    println("Ran service ${GlobalProperties.service.name}:${GlobalProperties.service.version}")
}
