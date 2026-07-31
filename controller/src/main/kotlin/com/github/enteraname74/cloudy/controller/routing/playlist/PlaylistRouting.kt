package com.github.enteraname74.cloudy.controller.routing.playlist

import com.github.enteraname74.cloudy.config.plugin.authenticatedRoutes
import com.github.enteraname74.cloudy.controller.routing.playlist.routes.*
import io.ktor.server.routing.*

fun Routing.playlistRouting() {
    authenticatedRoutes {
        deletePlaylists()
        playlistsOfUser()
        uploadPlaylist()
        getPlaylistCover()
        checkPlaylistIdsValidity()
    }
}