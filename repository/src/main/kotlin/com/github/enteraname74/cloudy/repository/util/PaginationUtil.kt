package com.github.enteraname74.cloudy.repository.util

import com.github.enteraname74.cloudy.domain.model.UpdatableElement
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import java.time.LocalDateTime

fun <T: UpdatableElement>List<T>.paginated(
    paginatedRequest: PaginatedRequest
): List<T> =
    this.filterLastUpdate(paginatedRequest.lastUpdateAt)

private fun <T: UpdatableElement>List<T>.filterLastUpdate(lastUpdateAt: LocalDateTime?): List<T> =
    this
        .filter {
            if (lastUpdateAt != null) {
                it.lastUpdateAt.isAfter(lastUpdateAt) || it.lastUpdateAt.isEqual(lastUpdateAt)
            } else {
                true
            }
        }