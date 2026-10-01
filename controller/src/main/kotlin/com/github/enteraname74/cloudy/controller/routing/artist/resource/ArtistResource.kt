package com.github.enteraname74.cloudy.controller.routing.artist.resource

import io.ktor.resources.Resource

@Resource("/artist")
class ArtistResource {
    @Resource("cover/{coverId}")
    data class Cover(
        val parent: ArtistResource = ArtistResource(),
        val coverId: String,
    )
}