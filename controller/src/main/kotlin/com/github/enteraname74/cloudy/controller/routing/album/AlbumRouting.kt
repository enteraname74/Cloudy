package com.github.enteraname74.cloudy.controller.routing.album

import com.github.enteraname74.cloudy.config.plugin.authenticatedRoutes
import com.github.enteraname74.cloudy.controller.routing.album.routes.albumsOfUser
import com.github.enteraname74.cloudy.controller.routing.album.routes.deleteAlbum
import com.github.enteraname74.cloudy.controller.routing.album.routes.updateAlbum
import io.ktor.server.routing.*

fun Routing.albumRouting() {
    authenticatedRoutes {
        route("/album") {
            albumsOfUser()
            deleteAlbum()
            updateAlbum()
        }
    }
}
