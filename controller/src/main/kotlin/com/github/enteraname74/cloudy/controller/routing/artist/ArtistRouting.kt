package com.github.enteraname74.cloudy.controller.routing.artist

import com.github.enteraname74.cloudy.config.plugin.authenticatedRoutes
import com.github.enteraname74.cloudy.controller.routing.artist.routes.artistsOfUser
import com.github.enteraname74.cloudy.controller.routing.artist.routes.deleteArtist
import com.github.enteraname74.cloudy.controller.routing.artist.routes.updateArtist
import io.ktor.server.routing.*

fun Routing.artistRouting() {
    authenticatedRoutes {
        route("/artist") {
            artistsOfUser()
            updateArtist()
            deleteArtist()
        }
    }
}