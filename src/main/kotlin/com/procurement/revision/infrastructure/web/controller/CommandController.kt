package com.procurement.revision.infrastructure.web.controller

import com.procurement.revision.domain.functional.Result
import com.procurement.revision.infrastructure.configuration.properties.GlobalProperties
import com.procurement.revision.infrastructure.fail.Fail
import com.procurement.revision.infrastructure.service.CommandService
import com.procurement.revision.infrastructure.utils.toJson
import com.procurement.revision.infrastructure.utils.tryToNode
import com.procurement.revision.infrastructure.web.dto.ApiResponse
import com.procurement.revision.infrastructure.web.dto.ApiVersion
import com.procurement.revision.infrastructure.web.dto.NaN
import com.procurement.revision.infrastructure.web.dto.errorResponse
import com.procurement.revision.infrastructure.web.dto.generateResponseOnFailure
import com.procurement.revision.infrastructure.web.dto.tryGetAction
import com.procurement.revision.infrastructure.web.dto.tryGetId
import com.procurement.revision.infrastructure.web.dto.tryGetVersion
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
@RequestMapping("/command")
class CommandController(private val commandService: CommandService) {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(CommandController::class.java)
    }

    @PostMapping
    fun command(@RequestBody requestBody: String): ResponseEntity<ApiResponse> {
        if (log.isDebugEnabled)
            log.debug("RECEIVED COMMAND: '$requestBody'.")

        val node = when (val result = requestBody.tryToNode()) {
            is Result.Success -> result.get
            is Result.Failure -> {
                log.debug("Error.", result.error.description)
                val response = generateResponse(fail = result.error)
                return ResponseEntity(response, HttpStatus.OK)
            }
        }
        val id = when (val result = node.tryGetId()) {
            is Result.Success -> result.get
            is Result.Failure -> {
                log.debug("Error.", result.error.description)
                val response = generateResponse(fail = result.error)
                return ResponseEntity(response, HttpStatus.OK)
            }
        }
        val version = when (val result = node.tryGetVersion()) {
            is Result.Success -> result.get
            is Result.Failure -> {
                log.debug("Error.", result.error.description)
                val response = generateResponse(fail = result.error, id = id)
                return ResponseEntity(response, HttpStatus.OK)
            }
        }

        when (val result = node.tryGetAction()) {
            is Result.Success -> result.get
            is Result.Failure -> {
                log.debug("Error.", result.error.description)
                val response = generateResponse(fail = result.error, id = id, version = version)
                return ResponseEntity(response, HttpStatus.OK)
            }
        }

        val response = try {
            commandService.execute(node)
                .also { response ->
                    if (log.isDebugEnabled)
                        log.debug("RESPONSE (id: '${id}'): '${response.toJson()}'.")
                }
        } catch (expected: Exception) {
            log.debug("Error.", expected)
            errorResponse(
                exception = expected,
                id = id,
                version = version
            )
        }
        return ResponseEntity(response, HttpStatus.OK)
    }

    private fun generateResponse(
        fail: Fail,
        version: ApiVersion = GlobalProperties.App.apiVersion,
        id: UUID = NaN
    ) = generateResponseOnFailure(fail = fail, id = id, version = version)
}