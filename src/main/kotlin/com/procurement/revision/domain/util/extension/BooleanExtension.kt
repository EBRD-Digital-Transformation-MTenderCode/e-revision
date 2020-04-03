package com.procurement.revision.domain.util.extension

inline fun Boolean.ifFalse(block: () -> Nothing) {
    if (!this) block()
}