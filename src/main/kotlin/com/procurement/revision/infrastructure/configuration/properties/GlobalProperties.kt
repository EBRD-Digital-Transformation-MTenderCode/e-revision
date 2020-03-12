package com.procurement.revision.infrastructure.configuration.properties

import com.procurement.revision.infrastructure.io.load
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
            val pathToFile = "file:/maven/e-revision-1.1.1.jar/BOOT-INF/classes/git.properties"
                //this.javaClass.getResourcePath("git.properties")
            val gitProps: Properties = Properties().load(pathToFile = pathToFile)
            return gitProps.orThrow("git.commit.id.abbrev")
        }
    }
}
