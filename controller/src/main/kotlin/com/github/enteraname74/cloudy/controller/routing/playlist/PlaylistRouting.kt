package com.github.enteraname74.cloudy.controller.routing.playlist

import com.github.enteraname74.cloudy.config.plugin.authenticatedRoutes
import com.github.enteraname74.cloudy.controller.routing.playlist.routes.addMusicsToPlaylist
import com.github.enteraname74.cloudy.controller.routing.playlist.routes.checkPlaylistIdsValidity
import com.github.enteraname74.cloudy.controller.routing.playlist.routes.createPlaylist
import com.github.enteraname74.cloudy.controller.routing.playlist.routes.deleteMusicsFromPlaylist
import com.github.enteraname74.cloudy.controller.routing.playlist.routes.deletePlaylist
import com.github.enteraname74.cloudy.controller.routing.playlist.routes.getPlaylistCover
import com.github.enteraname74.cloudy.controller.routing.playlist.routes.playlistsOfUser
import com.github.enteraname74.cloudy.controller.routing.playlist.routes.updatePlaylist
import com.github.enteraname74.cloudy.controller.routing.playlist.routes.uploadPlaylists
import io.ktor.server.routing.Routing

fun Routing.playlistRouting() {
    authenticatedRoutes {
        deletePlaylist()
        playlistsOfUser()
        uploadPlaylists()
        updatePlaylist()
        createPlaylist()
        getPlaylistCover()
        addMusicsToPlaylist()
        deleteMusicsFromPlaylist()
        checkPlaylistIdsValidity()
    }
}