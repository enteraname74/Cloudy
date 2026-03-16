package com.github.enteraname74.cloudy.controller.routing.music

import com.github.enteraname74.cloudy.config.plugin.authenticatedRoutes
import com.github.enteraname74.cloudy.controller.routing.music.routes.*
import io.ktor.server.routing.*

fun Routing.musicRouting() {
    authenticatedRoutes {
        uploadMusic()
        songsOfUser()
        getMusicFile()
        deleteSongs()
        updateMusics()
        checkMusicIdsValidity()
        getMusicCover()
    }
}