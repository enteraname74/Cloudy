package com.github.enteraname74.cloudy.controller.routing.album.resource

import io.ktor.resources.Resource

@Resource("/album")
class AlbumResource {
    @Resource("cover/{coverId}")
    data class Cover(
        val parent: AlbumResource = AlbumResource(),
        val coverId: String,
    )
}