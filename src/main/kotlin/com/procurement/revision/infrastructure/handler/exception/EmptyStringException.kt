package com.procurement.revision.infrastructure.handler.exception


class EmptyStringException(val attributeName: String) : RuntimeException(attributeName)
