package com.github.enteraname74.cloudy.controller.routing.music

import com.github.enteraname74.cloudy.config.plugin.authenticatedRoutes
import com.github.enteraname74.cloudy.controller.routing.music.routes.checkMusicIdsValidity
import com.github.enteraname74.cloudy.controller.routing.music.routes.deleteSongs
import com.github.enteraname74.cloudy.controller.routing.music.routes.fetchFromUrl
import com.github.enteraname74.cloudy.controller.routing.music.routes.getMusicCover
import com.github.enteraname74.cloudy.controller.routing.music.routes.getMusicFile
import com.github.enteraname74.cloudy.controller.routing.music.routes.getMusicFileFromToken
import com.github.enteraname74.cloudy.controller.routing.music.routes.songsOfUser
import com.github.enteraname74.cloudy.controller.routing.music.routes.updateMusics
import com.github.enteraname74.cloudy.controller.routing.music.routes.uploadMusic
import io.ktor.server.routing.Routing

fun Routing.musicRouting() {
    authenticatedRoutes {
        uploadMusic()
        fetchFromUrl()
        songsOfUser()
        getMusicFile()
        deleteSongs()
        updateMusics()
        checkMusicIdsValidity()
        getMusicCover()
    }
    getMusicFileFromToken()
}