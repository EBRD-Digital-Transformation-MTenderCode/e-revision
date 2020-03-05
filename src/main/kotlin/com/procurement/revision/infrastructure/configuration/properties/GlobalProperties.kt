package com.procurement.revision.infrastructure.configuration.properties

import com.procurement.revision.infrastructure.web.dto.ApiVersion
import java.util.*

object GlobalProperties {
    val service = Service()

    object App {
        val apiVersion = ApiVersion(major = 1, minor = 0, patch = 0)
    }

    class Service(
        val id: String = "21",
        val name: String = "e-revision",
        val version: String = getGitProperties()
    )
    private fun getGitProperties():String {
        val prop = Properties()
        val loader = Thread.currentThread().contextClassLoader
        val stream = loader.getResourceAsStream("git.properties")
        if (stream != null) {
            prop.load(stream)
            return prop.getProperty("git.commit.id.abbrev")
        } else {
            throw RuntimeException("Unable to find git.commit.id.abbrev")
        }
    }
}
