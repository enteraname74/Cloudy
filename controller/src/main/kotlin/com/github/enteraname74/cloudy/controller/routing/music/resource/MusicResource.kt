package com.github.enteraname74.cloudy.controller.routing.music.resource

import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import io.ktor.resources.*

@Resource("/music")
class MusicResource {

    @Resource("upload")
    data class Upload(
        val parent: MusicResource = MusicResource()
    )

    @Resource("check")
    data class Check(
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
    @Resource("{id}")
    data class File(
        val parent: MusicResource = MusicResource(),
        val id: String,
    )

    @Resource("cover/{coverId}")
    data class Cover(
        val parent: MusicResource = MusicResource(),
        val coverId: String,
    )
}