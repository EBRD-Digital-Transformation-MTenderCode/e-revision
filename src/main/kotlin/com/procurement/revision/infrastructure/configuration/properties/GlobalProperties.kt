package com.procurement.revision.infrastructure.configuration.properties

import com.procurement.revision.infrastructure.web.dto.ApiVersion

object GlobalProperties {
    const val serviceId = "21"

    object App {
        val apiVersion = ApiVersion(major = 1, minor = 0, patch = 0)
    }
}
