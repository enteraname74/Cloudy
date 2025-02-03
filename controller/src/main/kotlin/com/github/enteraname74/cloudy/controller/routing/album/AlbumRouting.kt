package com.github.enteraname74.cloudy.controller.routing.album

import com.github.enteraname74.cloudy.config.plugin.authenticatedRoutes
import com.github.enteraname74.cloudy.controller.routing.album.routes.albumsOfUser
import com.github.enteraname74.cloudy.controller.routing.album.routes.checkAlbumIdsValidity
import com.github.enteraname74.cloudy.controller.routing.album.routes.deleteAlbums
import com.github.enteraname74.cloudy.controller.routing.album.routes.updateAlbum
import io.ktor.server.routing.*

fun Routing.albumRouting() {
    authenticatedRoutes("/album") {
        albumsOfUser()
        deleteAlbums()
        updateAlbum()
        checkAlbumIdsValidity()
    }
}
