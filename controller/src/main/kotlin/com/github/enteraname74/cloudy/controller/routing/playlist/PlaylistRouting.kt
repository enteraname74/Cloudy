package com.github.enteraname74.cloudy.controller.routing.playlist

import com.github.enteraname74.cloudy.config.plugin.authenticatedRoutes
import io.ktor.server.routing.*

fun Routing.playlistRouting() {
    authenticatedRoutes {
        route("/playlist") {

        }
    }
}