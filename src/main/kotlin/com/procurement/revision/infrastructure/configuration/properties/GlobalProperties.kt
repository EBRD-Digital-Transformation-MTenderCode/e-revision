package com.procurement.revision.infrastructure.configuration.properties

import com.procurement.revision.infrastructure.io.orThrow
import com.procurement.revision.infrastructure.web.dto.ApiVersion
import java.util.*

object GlobalProperties {
    val service = Service()

    object App {
        val apiVersion = ApiVersion(major = 1, minor = 0, patch = 0)
    }

    class Service {
        val id: String = "21"
        val name: String = "e-revision"
        val version: String = loadVersion()

        private fun loadVersion(): String {
            val gitProps: Properties = try {
                GlobalProperties::class.java.getResourceAsStream("/git.properties")
                    .use { stream ->
                        Properties().apply { load(stream) }
                    }
            } catch (expected: Exception) {
                throw IllegalStateException(expected)
            }
            return gitProps.orThrow("git.commit.id.abbrev")
        }
    }
}
