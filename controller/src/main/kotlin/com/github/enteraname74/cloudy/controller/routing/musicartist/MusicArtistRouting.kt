package com.github.enteraname74.cloudy.controller.routing.musicartist

import com.github.enteraname74.cloudy.config.plugin.authenticatedRoutes
import com.github.enteraname74.cloudy.controller.routing.musicartist.routes.allMusicArtistOfUser
import io.ktor.server.routing.*

fun Routing.musicArtistRouting() {
    authenticatedRoutes("/musicartist") {
        allMusicArtistOfUser()
    }
}