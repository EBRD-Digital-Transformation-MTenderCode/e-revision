package com.procurement.revision.infrastructure.service

import com.procurement.revision.application.model.amendment.CheckExistingAmendmentForCancelLotContext
import com.procurement.revision.application.model.amendment.CheckExistingAmendmentForCancelTenderContext
import com.procurement.revision.application.model.amendment.ProceedAmendmentLotCancellationContext
import com.procurement.revision.application.model.amendment.ProceedAmendmentTenderCancellationContext
import com.procurement.revision.application.service.AmendmentService
import com.procurement.revision.application.service.amendment.CheckExistingAmendmentForCancelLotResponse
import com.procurement.revision.application.service.amendment.CheckExistingAmendmentForCancelTenderResponse
import com.procurement.revision.infrastructure.dto.amendment.ProceedAmendmentRequest
import com.procurement.revision.infrastructure.dto.amendment.ProceedAmendmentResponse
import com.procurement.revision.infrastructure.dto.converter.convert
import com.procurement.revision.infrastructure.repository.HistoryRepository
import com.procurement.revision.infrastructure.utils.toJson
import com.procurement.revision.infrastructure.utils.toObject
import com.procurement.revision.infrastructure.web.dto.ApiSuccessResponse
import com.procurement.revision.infrastructure.web.dto.CommandMessage
import com.procurement.revision.infrastructure.web.dto.CommandType
import com.procurement.revision.infrastructure.web.dto.cpid
import com.procurement.revision.infrastructure.web.dto.lotId
import com.procurement.revision.infrastructure.web.dto.owner
import com.procurement.revision.infrastructure.web.dto.startDate
import com.procurement.revision.infrastructure.web.dto.tenderId
import com.procurement.revision.infrastructure.web.dto.token
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CommandService(
    private val historyRepository: HistoryRepository,
    private val amendmentService: AmendmentService
) {
    companion object {
        private val log = LoggerFactory.getLogger(CommandService::class.java)
    }

    fun execute(cm: CommandMessage): ApiSuccessResponse {
        val dataOfResponse: Any = when (cm.command) {
            CommandType.PROCEED_AMENDMENT_FOR_LOT_CANCELLATION -> {
                generateResponseFromHistory(cm)?.run { return this }
                val context = ProceedAmendmentLotCancellationContext(
                    cpid = cm.cpid,
                    id = cm.lotId,
                    startDate = cm.startDate,
                    owner = cm.owner,
                    token = cm.token
                )
                val request: ProceedAmendmentRequest = cm.data.toObject(ProceedAmendmentRequest::class.java)
                val result = amendmentService.proceedAmendmentForLotCancellation(
                    context = context,
                    data = request.convert()
                )
                if (log.isDebugEnabled)
                    log.debug("Amendments for lot cancellation have been created. Result: ${result.toJson()}")

                val dataResponse: ProceedAmendmentResponse = result.convert()
                if (log.isDebugEnabled)
                    log.debug("Amendments for lot cancellation have been created. Response: ${dataResponse.toJson()}")
                historyRepository.saveHistory(cm.id, cm.command.value(), dataResponse)
                dataResponse
            }

            CommandType.PROCEED_AMENDMENT_FOR_TENDER_CANCELLATION -> {
                generateResponseFromHistory(cm)?.run { return this }
                val context = ProceedAmendmentTenderCancellationContext(
                    cpid = cm.cpid,
                    id = cm.tenderId,
                    startDate = cm.startDate,
                    owner = cm.owner,
                    token = cm.token
                )
                val request: ProceedAmendmentRequest = cm.data.toObject(ProceedAmendmentRequest::class.java)
                val result = amendmentService.proceedAmendmentForTenderCancellation(
                    context = context,
                    data = request.convert()
                )
                if (log.isDebugEnabled)
                    log.debug("Amendments for tender cancellation have been created. Result: ${result.toJson()}")

                val dataResponse: ProceedAmendmentResponse = result.convert()
                if (log.isDebugEnabled)
                    log.debug("Amendments for lot cancellation have been created. Response: ${dataResponse.toJson()}")
                historyRepository.saveHistory(cm.id, cm.command.value(), dataResponse)
                dataResponse
            }

            CommandType.CHECK_EXISTING_AMENDMENT_FOR_CANCEL_LOT -> {
                val context = CheckExistingAmendmentForCancelLotContext(
                    cpid = cm.cpid,
                    id = cm.lotId,
                    owner = cm.owner,
                    token = cm.token
                )
                val result = amendmentService.checkExistingAmendmentForCancelLot(context = context)
                if (log.isDebugEnabled)
                    log.debug("Existing of amendments for cancel lot have been checked. Result: ${result.toJson()}")

                val dataResponse = CheckExistingAmendmentForCancelLotResponse()
                if (log.isDebugEnabled)
                    log.debug(
                        "Existing of amendments for cancel lot have been checked. Response: ${dataResponse.toJson()}"
                    )
                dataResponse
            }

            CommandType.CHECK_EXISTING_AMENDMENT_FOR_CANCEL_TENDER -> {
                val context = CheckExistingAmendmentForCancelTenderContext(
                    cpid = cm.cpid,
                    id = cm.lotId,
                    owner = cm.owner,
                    token = cm.token
                )
                val result = amendmentService.checkExistingAmendmentForCancelTender(context = context)
                if (log.isDebugEnabled)
                    log.debug("Existing of amendments for cancel tender have been checked. Result: ${result.toJson()}")

                val dataResponse = CheckExistingAmendmentForCancelTenderResponse()
                if (log.isDebugEnabled)
                    log.debug(
                        "Existing of amendments for cancel tender have been checked. Response: ${dataResponse.toJson()}"
                    )
                dataResponse
            }
        }
        return ApiSuccessResponse(id = cm.id, version = cm.version, data = dataOfResponse)
            .also {
                if (log.isDebugEnabled)
                    log.debug("Response: ${it.toJson()}")
            }
    }

    private fun generateResponseFromHistory(cm: CommandMessage): ApiSuccessResponse? {
        val historyEntity = historyRepository.getHistory(cm.id, cm.command.value())
        return if (historyEntity != null) {
            ApiSuccessResponse(
                id = cm.id,
                version = cm.version,
                data = historyEntity.jsonData
            )
        } else null
    }
}
