package com.github.enteraname74.cloudy.localdb.util

import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.jdbc.SizedIterable

internal fun <T> SizedIterable<T>.paginated(
    paginatedRequest: PaginatedRequest,
): SizedIterable<T> =
    if (paginatedRequest.page != null && paginatedRequest.limitPerPage != null) {
        this
            .offset((paginatedRequest.page!! * paginatedRequest.limitPerPage!!).toLong())
            .limit(paginatedRequest.limitPerPage!!)
    } else {
        this
    }

internal infix fun Column<Long>.updatedAfter(other: Long?): Op<Boolean> =
    (other?.let { lastUpdateAt ->
        this greater lastUpdateAt
    } ?: Op.TRUE)