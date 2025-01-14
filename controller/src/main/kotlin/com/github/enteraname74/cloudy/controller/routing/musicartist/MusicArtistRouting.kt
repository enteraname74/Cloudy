package com.github.enteraname74.cloudy.controller.routing.musicartist

import com.github.enteraname74.cloudy.config.plugin.authenticatedRoutes
import com.github.enteraname74.cloudy.controller.routing.musicartist.routes.allMusicArtistOfUser
import io.ktor.server.routing.Routing
import io.ktor.server.routing.route

fun Routing.musicArtistRouting() {
    authenticatedRoutes {
        route("/musicartist") {
            allMusicArtistOfUser()
        }
    }
}