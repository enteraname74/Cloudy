package com.github.enteraname74.cloudy.controller.routing.album

import com.github.enteraname74.cloudy.config.plugin.authenticatedRoutes
import com.github.enteraname74.cloudy.controller.routing.album.routes.albumsOfUser
import io.ktor.server.routing.*

fun Routing.albumRouting() {
    authenticatedRoutes {
        route("/album") {
            albumsOfUser()
        }
    }
}
