package com.github.enteraname74.cloudy.controller.routing.playlist

import com.github.enteraname74.cloudy.config.plugin.authenticatedRoutes
import com.github.enteraname74.cloudy.controller.routing.playlist.routes.deletePlaylist
import com.github.enteraname74.cloudy.controller.routing.playlist.routes.getPlaylistCover
import com.github.enteraname74.cloudy.controller.routing.playlist.routes.playlistsOfUser
import com.github.enteraname74.cloudy.controller.routing.playlist.routes.uploadPlaylists
import io.ktor.server.routing.Routing

fun Routing.playlistRouting() {
    authenticatedRoutes {
        deletePlaylist()
        playlistsOfUser()
        uploadPlaylists()
        getPlaylistCover()
    }
}