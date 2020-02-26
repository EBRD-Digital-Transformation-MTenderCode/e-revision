package com.procurement.revision.infrastructure.utils

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.procurement.revision.application.exception.ErrorException
import com.procurement.revision.application.exception.ErrorType
import com.procurement.revision.domain.util.Result
import com.procurement.revision.infrastructure.bind.databinding.JsonDateTimeFormatter
import com.procurement.revision.infrastructure.bind.jackson.configuration
import java.io.IOException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

private object JsonMapper {
    val mapper: ObjectMapper = ObjectMapper().apply {
        configuration()
    }
}

/*Date utils*/
fun String.toLocal(): LocalDateTime {
    return LocalDateTime.parse(this, JsonDateTimeFormatter.formatter)
}

fun LocalDateTime.toDate(): Date {
    return Date.from(this.toInstant(ZoneOffset.UTC))
}

fun localNowUTC(): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC)
}

/**
Json utils
 **/
fun <T : Any> T.toJson(): String = try {
    JsonMapper.mapper.writeValueAsString(this)
} catch (expected: JsonProcessingException) {
    val className = this::class.java.canonicalName
    throw IllegalArgumentException("Error mapping an object of type '$className' to JSON.", expected)
}

fun <T : Any> String.toObject(target: Class<T>): T = try {
    JsonMapper.mapper.readValue(this, target)
} catch (expected: Exception) {
    throw IllegalArgumentException("Error binding JSON to an object of type '${target.canonicalName}'.", expected)
}

fun <T : Any> JsonNode.tryToObject(target: Class<T>): Result<T, String> = try {
    Result.success(JsonMapper.mapper.treeToValue(this, target))
} catch (expected: Exception) {
    Result.failure("Error binding JSON to an object of type '${target.canonicalName}'.")
}

fun <T : Any> JsonNode.toObject(target: Class<T>): T {
    try {
        return JsonMapper.mapper.treeToValue(this, target)
    } catch (expected: IOException) {
        throw IllegalArgumentException("Error binding JSON to an object of type '${target.canonicalName}'.", expected)
    }
}

fun String.toNode(): JsonNode = try {
    JsonMapper.mapper.readTree(this)
} catch (exception: JsonProcessingException) {
    throw IllegalArgumentException("Error parsing String to JsonNode.", exception)
}

fun String.tryToNode(): Result<JsonNode, ErrorException> = try {
    Result.success(JsonMapper.mapper.readTree(this))
} catch (exception: JsonProcessingException) {
    Result.failure(ErrorException(ErrorType.INVALID_JSON, "Error parsing String to JsonNode."))
}
