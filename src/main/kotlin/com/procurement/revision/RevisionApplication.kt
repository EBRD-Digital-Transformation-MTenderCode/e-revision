package com.procurement.revision

import com.procurement.revision.infrastructure.configuration.ApplicationConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackageClasses = [ApplicationConfiguration::class])
class RevisionApplication

fun main(args: Array<String>) {
    runApplication<RevisionApplication>(*args)
}

