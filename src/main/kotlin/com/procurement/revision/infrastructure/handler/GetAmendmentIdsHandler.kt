package com.procurement.revision.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.revision.application.service.AmendmentService
import com.procurement.revision.domain.model.amendment.AmendmentId
import com.procurement.revision.infrastructure.converter.convert
import com.procurement.revision.infrastructure.utils.toObject
import com.procurement.revision.infrastructure.web.dto.Command2Type
import com.procurement.revision.infrastructure.web.dto.getBy
import com.procurement.revision.infrastructure.web.dto.request.amendment.GetAmendmentIdsRequest
import org.springframework.stereotype.Component

@Component
class GetAmendmentIdsHandler(private val amendmentService: AmendmentService) : AbstractHandler<Command2Type, List<AmendmentId>>() {

    override val action: Command2Type = Command2Type.GET_AMENDMENTS_IDS

    override fun execute(node: JsonNode): List<AmendmentId> {
        val request = node.getBy("params").toObject(GetAmendmentIdsRequest::class.java)
        val params = request.convert()
        return amendmentService.getAmendmentIdsBy(params)
    }
}