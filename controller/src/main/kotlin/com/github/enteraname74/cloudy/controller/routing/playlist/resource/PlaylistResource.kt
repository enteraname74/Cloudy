package com.github.enteraname74.cloudy.controller.routing.playlist.resource

import com.github.enteraname74.cloudy.controller.routing.music.resource.MusicResource
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import io.ktor.resources.Resource

@Resource("/playlist")
class PlaylistResource {

    @Resource("ofUser")
    data class OfUser(
        val parent: PlaylistResource = PlaylistResource(),
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

    @Resource("cover/{coverId}")
    data class Cover(
        val parent: MusicResource = MusicResource(),
        val coverId: String,
    )
}