package com.github.enteraname74.cloudy.localdb.util

import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import java.time.LocalDateTime

internal fun Query.paginated(
    paginatedRequest: PaginatedRequest,

): Query =
    if (paginatedRequest.page != null && paginatedRequest.limitPerPage != null) {
        this
            .offset((paginatedRequest.page!! * paginatedRequest.limitPerPage!!).toLong())
            .limit(paginatedRequest.limitPerPage!!)
    } else {
        this
    }

internal infix fun Column<LocalDateTime>.updatedAfter(other: LocalDateTime?): Op<Boolean> =
    (other?.let { lastUpdateAt ->
        this greaterEq lastUpdateAt
    } ?: Op.TRUE)