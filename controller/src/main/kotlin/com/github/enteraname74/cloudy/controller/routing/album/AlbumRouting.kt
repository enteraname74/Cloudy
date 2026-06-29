package com.github.enteraname74.cloudy.controller.routing.album

import com.github.enteraname74.cloudy.config.plugin.authenticatedRoutes
import com.github.enteraname74.cloudy.controller.routing.album.routes.deleteAlbums
import com.github.enteraname74.cloudy.controller.routing.album.routes.getAlbumCover
import io.ktor.server.routing.Routing

fun Routing.albumRouting() {
    authenticatedRoutes {
        deleteAlbums()
        getAlbumCover()
    }
}
