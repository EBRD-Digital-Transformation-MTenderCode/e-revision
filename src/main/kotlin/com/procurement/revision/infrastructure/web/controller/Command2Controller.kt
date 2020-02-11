package com.procurement.revision.infrastructure.web.controller

import com.procurement.revision.infrastructure.configuration.properties.GlobalProperties
import com.procurement.revision.infrastructure.service.Command2Service
import com.procurement.revision.infrastructure.utils.toJson
import com.procurement.revision.infrastructure.utils.toObject
import com.procurement.revision.infrastructure.web.dto.ApiResponse2
import com.procurement.revision.infrastructure.web.dto.Command2Message
import com.procurement.revision.infrastructure.web.dto.errorResponse2
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/command2")
class Command2Controller(private val commandService: Command2Service) {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(Command2Controller::class.java)
    }

    @PostMapping
    fun command(@RequestBody requestBody: String): ResponseEntity<ApiResponse2> {
        if (log.isDebugEnabled)
            log.debug("RECEIVED COMMAND: '$requestBody'.")

        val cm: Command2Message = try {
            requestBody.toObject(Command2Message::class.java)
        } catch (expected: Exception) {
            log.debug("Error.", expected)
            val response =
                errorResponse2(
                    exception = expected,
                    version = GlobalProperties.App.apiVersion
                )
            return ResponseEntity(response, HttpStatus.OK)
        }

        val response = try {
            commandService.execute(cm)
                .also { response ->
                    if (log.isDebugEnabled)
                        log.debug("RESPONSE (id: '${cm.id}'): '${response.toJson()}'.")
                }
        } catch (expected: Exception) {
            log.debug("Error.", expected)
            errorResponse2(
                exception = expected,
                id = cm.id,
                version = cm.version
            )
        }
        return ResponseEntity(response, HttpStatus.OK)
    }
}