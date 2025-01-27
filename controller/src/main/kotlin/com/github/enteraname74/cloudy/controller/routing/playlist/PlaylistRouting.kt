package com.github.enteraname74.cloudy.controller.routing.playlist

import com.github.enteraname74.cloudy.config.plugin.authenticatedRoutes
import com.github.enteraname74.cloudy.controller.routing.playlist.routes.deletePlaylist
import com.github.enteraname74.cloudy.controller.routing.playlist.routes.playlistsOfUser
import com.github.enteraname74.cloudy.controller.routing.playlist.routes.updatePlaylist
import com.github.enteraname74.cloudy.controller.routing.playlist.routes.uploadPlaylists
import io.ktor.server.routing.*

fun Routing.playlistRouting() {
    authenticatedRoutes {
        route("/playlist") {
            deletePlaylist()
            playlistsOfUser()
            uploadPlaylists()
            updatePlaylist()
        }
    }
}