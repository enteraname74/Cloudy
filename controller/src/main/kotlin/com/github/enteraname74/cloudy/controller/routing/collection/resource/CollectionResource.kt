package com.github.enteraname74.cloudy.controller.routing.collection.resource

import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import io.ktor.resources.Resource

@Resource("/collection")
class CollectionResource {
    @Resource("ofUser")
    data class OfUser(
        val parent: CollectionResource = CollectionResource(),
        val lastUpdateAt: Long? = null,
        val maxPerPage: Int? = null,
        val page: Int? = null,
    ) {
        fun toPaginatedRequest(): PaginatedRequest = PaginatedRequest(
            lastUpdateAtMillis = lastUpdateAt,
            page = page,
            limitPerPage = maxPerPage,
        )
    }

    @Resource("cover/{coverId}")
    data class Cover(
        val parent: CollectionResource = CollectionResource(),
        val coverId: String,
    )

    @Resource("check")
    data class Check(val parent: CollectionResource = CollectionResource())
}
