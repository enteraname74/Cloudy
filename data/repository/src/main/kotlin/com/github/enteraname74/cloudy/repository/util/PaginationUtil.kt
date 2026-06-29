package com.github.enteraname74.cloudy.repository.util

import com.github.enteraname74.cloudy.domain.model.UpdatableElement
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest

fun <T: UpdatableElement>List<T>.paginated(
    paginatedRequest: PaginatedRequest
): List<T> =
    this.filterLastUpdate(paginatedRequest.lastUpdateAtMillis)

private fun <T: UpdatableElement>List<T>.filterLastUpdate(lastUpdateAtMillis: Long?): List<T> =
    this
        .filter {
            if (lastUpdateAtMillis != null) {
                it.lastUpdateAtMillis >= lastUpdateAtMillis
            } else {
                true
            }
        }