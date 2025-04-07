package com.github.enteraname74.cloudy.controller.routing.album

import com.github.enteraname74.cloudy.config.plugin.authenticatedRoutes
import com.github.enteraname74.cloudy.controller.routing.album.routes.*
import io.ktor.server.routing.*

fun Routing.albumRouting() {
    authenticatedRoutes("/album") {
        albumsOfUser()
        deleteAlbums()
        updateAlbum()
        getAlbumCover()
        checkAlbumIdsValidity()
    }
}
