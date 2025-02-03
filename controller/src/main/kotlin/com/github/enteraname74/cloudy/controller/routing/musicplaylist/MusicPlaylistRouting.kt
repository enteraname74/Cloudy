package com.github.enteraname74.cloudy.controller.routing.musicplaylist

import com.github.enteraname74.cloudy.config.plugin.authenticatedRoutes
import com.github.enteraname74.cloudy.controller.routing.musicplaylist.routes.allMusicPlaylistOfUser
import io.ktor.server.routing.*

fun Routing.musicPlaylistRouting() {
    authenticatedRoutes("/musicplaylist") {
        allMusicPlaylistOfUser()
    }
}