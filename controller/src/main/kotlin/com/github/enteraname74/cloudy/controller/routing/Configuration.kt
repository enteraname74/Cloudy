package com.github.enteraname74.cloudy.controller.routing

import com.github.enteraname74.cloudy.config.plugin.authenticatedRoutes
import com.github.enteraname74.cloudy.config.plugin.isAdmin
import com.github.enteraname74.cloudy.controller.routing.album.albumRouting
import com.github.enteraname74.cloudy.controller.routing.artist.artistRouting
import com.github.enteraname74.cloudy.controller.routing.auth.authRouting
import com.github.enteraname74.cloudy.controller.routing.music.musicRouting
import com.github.enteraname74.cloudy.controller.routing.playlist.playlistRouting
import com.github.enteraname74.cloudy.controller.routing.user.userRoutes
import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        userRoutes()
        authRouting()
        musicRouting()
        albumRouting()
        artistRouting()
        playlistRouting()
        get("/hello") {
            call.respondText("Hello Ktor My Beloved!")
        }
        authenticatedRoutes("/admin") {
            get("/admin") {
                if (isAdmin()) {
                    call.respondText("Hello admin!")
                } else {
                    call.respondText("Not an admin!")
                }
            }
        }
    }
}