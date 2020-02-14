package com.procurement.revision.infrastructure.web.controller

import com.procurement.revision.infrastructure.configuration.properties.GlobalProperties
import com.procurement.revision.infrastructure.service.Command2Service
import com.procurement.revision.infrastructure.utils.toJson
import com.procurement.revision.infrastructure.utils.toNode
import com.procurement.revision.infrastructure.web.dto.ApiResponse2
import com.procurement.revision.infrastructure.web.dto.ApiVersion
import com.procurement.revision.infrastructure.web.dto.errorResponse2
import com.procurement.revision.infrastructure.web.dto.getBy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

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

        val node = try {
            requestBody.toNode()
        } catch (expected: Exception) {
            log.debug("Error.", expected)
            val response =
                errorResponse2(
                    exception = expected,
                    version = GlobalProperties.App.apiVersion
                )
            return ResponseEntity(response, HttpStatus.OK)
        }

        val id = try {
            UUID.fromString(node.getBy("id").asText())
        } catch (expected: Exception) {
            log.debug("Error.", expected)
            val response = errorResponse2(
                exception = expected,
                version = GlobalProperties.App.apiVersion
            )
            return ResponseEntity(response, HttpStatus.OK)
        }
        val version = try {
            ApiVersion.valueOf(node.getBy("version").asText())
        } catch (expected: Exception) {
            log.debug("Error.", expected)
            val response = errorResponse2(
                id = id,
                exception = expected,
                version = GlobalProperties.App.apiVersion
            )
            return ResponseEntity(response, HttpStatus.OK)
        }

        val response = try {
            commandService.execute(node)
                .also { response ->
                    if (log.isDebugEnabled)
                        log.debug("RESPONSE (id: '${id}'): '${response.toJson()}'.")
                }
        } catch (expected: Exception) {
            log.debug("Error.", expected)
            errorResponse2(
                exception = expected,
                id = id,
                version = version
            )
        }
        return ResponseEntity(response, HttpStatus.OK)
    }
}