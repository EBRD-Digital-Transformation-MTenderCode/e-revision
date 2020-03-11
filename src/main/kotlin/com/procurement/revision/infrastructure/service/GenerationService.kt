package com.procurement.revision.infrastructure.service

import com.procurement.revision.application.service.Generable
import java.util.*

class GenerationService : Generable {

    override fun generateAmendmentId(): UUID = UUID.randomUUID()

    override fun generateToken(): UUID = UUID.randomUUID()

}
