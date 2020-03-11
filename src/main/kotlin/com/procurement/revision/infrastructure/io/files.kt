package com.procurement.revision.infrastructure.io

fun Class<*>.getResourcePath(fileName: String): String = this.classLoader.getResource(fileName)?.path
    ?: throw IllegalStateException("File '$fileName' is not found.")
