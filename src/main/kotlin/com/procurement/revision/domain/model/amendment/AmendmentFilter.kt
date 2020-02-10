package com.procurement.revision.domain.model.amendment

import com.procurement.revision.domain.enums.AmendmentRelatesTo
import com.procurement.revision.domain.enums.AmendmentStatus
import com.procurement.revision.domain.enums.AmendmentType

data class AmendmentFilter(
    val status: AmendmentStatus? = null,
    val type: AmendmentType? = null,
    val relatesTo: AmendmentRelatesTo? = null,
    val relatedItem: String? = null
) {

    fun compareWith(amendment: Amendment): Boolean =
        status?.run { equals(amendment.status) } ?: true &&
            type?.run { equals(amendment.type) } ?: true &&
            relatesTo?.run { equals(amendment.relatesTo) } ?: true &&
            relatedItem?.run { equals(amendment.relatedItem) } ?: true
}
