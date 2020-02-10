package com.procurement.revision.infrastructure.service

import com.procurement.revision.application.handler.GetAmendmentIdsHandler
import com.procurement.revision.application.model.amendment.GetAmendmentIdsResult
import com.procurement.revision.infrastructure.converter.convert
import com.procurement.revision.infrastructure.utils.toJson
import com.procurement.revision.infrastructure.utils.toObject
import com.procurement.revision.infrastructure.web.dto.ApiResponse2
import com.procurement.revision.infrastructure.web.dto.Command2Message
import com.procurement.revision.infrastructure.web.dto.Command2Type
import com.procurement.revision.infrastructure.web.dto.ResponseStatus
import com.procurement.revision.infrastructure.web.dto.request.amendment.GetAmendmentIdsRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class Command2Service(val getAmendmentIdsHandler: GetAmendmentIdsHandler) {
    companion object {
        private val log = LoggerFactory.getLogger(Command2Service::class.java)
    }

    fun execute(cm: Command2Message): ApiResponse2 {
        val dataOfResponse: Any = when (cm.action) {
            Command2Type.GET_AMENDMENTS_IDS -> {
                val request = cm.params.toObject(GetAmendmentIdsRequest::class.java)
                val result = getAmendmentIdsHandler.handle(request.convert())
                if (log.isDebugEnabled)
                    log.debug("Amendment ids have been found. Result: ${result.toJson()}")

                val dataResponse = result.map { GetAmendmentIdsResult(it.id) }
                if (log.isDebugEnabled)
                    log.debug(
                        "Amendment ids have been found. Response: ${dataResponse.toJson()}"
                    )
                dataResponse
            }
        }
        return ApiResponse2(
            version = cm.version,
            id = cm.id,
            status = ResponseStatus.SUCCESS,
            result = dataOfResponse
        )
    }
}