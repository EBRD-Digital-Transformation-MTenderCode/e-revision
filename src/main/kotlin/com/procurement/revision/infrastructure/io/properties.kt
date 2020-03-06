package com.procurement.revision.infrastructure.io

import java.io.FileNotFoundException
import java.io.FileReader
import java.util.*

fun Properties.load(pathToFile: String) = try {
    this.apply {
        FileReader(pathToFile)
            .use { reader ->
                this.load(reader)
            }
    }
} catch (expected: FileNotFoundException) {
    throw IllegalStateException("File '$pathToFile' is not found.")
}

fun Properties.orThrow(name: String): String = this[name]
    ?.toString()
    ?: throw IllegalStateException("Property '$name' is not found.")
