package com.github.enteraname74.cloudy.controller.routing.statistics.resource

import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import io.ktor.resources.Resource

@Resource("/statistics")
class StatisticsResource {
    @Resource("ofUser")
    data class OfUser(
        val parent: StatisticsResource = StatisticsResource(),
        val lastUpdateAt: Long? = null,
        val maxPerPage: Int? = null,
        val page: Int? = null,
    ) {
        fun toPaginatedRequest(): PaginatedRequest =
            PaginatedRequest(
                lastUpdateAtMillis = lastUpdateAt,
                page = page,
                limitPerPage = maxPerPage,
            )
    }
}