package com.procurement.revision.infrastructure.web.controller

import com.procurement.revision.application.service.Logger
import com.procurement.revision.infrastructure.configuration.properties.GlobalProperties
import com.procurement.revision.infrastructure.fail.Fail
import com.procurement.revision.infrastructure.service.CommandService
import com.procurement.revision.infrastructure.utils.toJson
import com.procurement.revision.infrastructure.web.dto.ApiResponse
import com.procurement.revision.infrastructure.web.dto.ApiVersion
import com.procurement.revision.infrastructure.web.dto.NaN
import com.procurement.revision.infrastructure.web.dto.generateResponseOnFailure
import com.procurement.revision.infrastructure.web.dto.tryGetAction
import com.procurement.revision.infrastructure.web.dto.tryGetId
import com.procurement.revision.infrastructure.web.dto.tryGetNode
import com.procurement.revision.infrastructure.web.dto.tryGetVersion
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/command")
class CommandController(private val commandService: CommandService, private val logger: Logger) {

    @PostMapping
    fun command(@RequestBody requestBody: String): ResponseEntity<ApiResponse> {
        if (logger.isDebugEnabled)
            logger.debug("RECEIVED COMMAND: '$requestBody'.")

        val node = requestBody.tryGetNode()
            .doOnError { error -> return generateResponse(fail = error) }
            .get

        val id = node.tryGetId()
            .doOnError { error -> return generateResponse(fail = error) }
            .get

        val version = node.tryGetVersion()
            .doOnError { error -> return generateResponse(fail = error, id = id) }
            .get

        node.tryGetAction()
            .doOnError { error -> return generateResponse(fail = error, id = id, version = version) }

        val response =
            commandService.execute(node)
                .also { response ->
                    if (logger.isDebugEnabled)
                        logger.debug("RESPONSE (id: '${id}'): '${response.toJson()}'.")
                }

        return ResponseEntity(response, HttpStatus.OK)
    }

    private fun generateResponse(
        fail: Fail,
        version: ApiVersion = GlobalProperties.App.apiVersion,
        id: UUID = NaN
    ): ResponseEntity<ApiResponse> {
        val response = generateResponseOnFailure(fail = fail, id = id, version = version, logger = logger)
        return ResponseEntity(response, HttpStatus.OK)
    }
}