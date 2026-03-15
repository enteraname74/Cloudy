package com.github.enteraname74.cloudy.controller.routing.music.resource

import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import io.ktor.resources.Resource

@Resource("/music")
class MusicResource {

    @Resource("upload")
    class Upload(
        val parent: MusicResource = MusicResource()
    )

    @Resource("ofUser")
    data class OfUser(
        val parent: MusicResource = MusicResource(),
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