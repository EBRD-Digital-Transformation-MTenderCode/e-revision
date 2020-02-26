package com.procurement.revision.application.exception

enum class ErrorType constructor(val code: String, val message: String) {
    CONTEXT("20.01", "Context parameter not found."),
    INVALID_FORMAT_TOKEN("10.63", "Invalid format the award id."),
    INVALID_DOCUMENT_TYPE("10.77", "Invalid document type."),
    COLLECTION_IS_EMPTY("10.78", "Collection is empty."),
    INVALID_FORMAT_LOT_ID("10.79", "Invalid format the lot id."),
    UNEXPECTED_AMENDMENT("10.80", "Unexpected amendment."),
    DATA_NOT_FOUND("10.81", "Data not found."),
    IS_EMPTY("10.82", "List of items is empty."),
    INVALID_JSON("10.83", "Invalid json."),;
}
