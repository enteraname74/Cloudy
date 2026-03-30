package com.github.enteraname74.cloudy.controller.routing.player.resource

import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import io.ktor.resources.Resource
import kotlin.uuid.Uuid

@Resource("/player")
class PlayerResource {

    @Resource("join")
    class Join(
        val parent: PlayerResource = PlayerResource()
    )

    @Resource("")
    class List(
        val parent: PlayerResource = PlayerResource(),
        val listId: Uuid,
        val deviceId: String,
    )

    @Resource("removeUser")
    class RemoveUser(
        val parent: PlayerResource = PlayerResource(),
    )

    @Resource("allMusics")
    class GetMusics(
        val parent: PlayerResource = PlayerResource(),
        val listId: Uuid,
        val deviceId: String,
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

    @Resource("musics")
    class Musics(
        val parent: PlayerResource = PlayerResource()
    )
}