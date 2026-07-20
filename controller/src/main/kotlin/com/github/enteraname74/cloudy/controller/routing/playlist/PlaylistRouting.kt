package com.github.enteraname74.cloudy.controller.routing.playlist

import com.github.enteraname74.cloudy.config.plugin.authenticatedRoutes
import com.github.enteraname74.cloudy.controller.routing.playlist.routes.checkPlaylistIdsValidity
import com.github.enteraname74.cloudy.controller.routing.playlist.routes.deletePlaylists
import com.github.enteraname74.cloudy.controller.routing.playlist.routes.getPlaylistCover
import com.github.enteraname74.cloudy.controller.routing.playlist.routes.playlistsOfUser
import com.github.enteraname74.cloudy.controller.routing.playlist.routes.uploadPlaylist
import io.ktor.server.routing.Routing

fun Routing.playlistRouting() {
    authenticatedRoutes {
        deletePlaylists()
        playlistsOfUser()
        uploadPlaylist()
        getPlaylistCover()
        checkPlaylistIdsValidity()
    }
}