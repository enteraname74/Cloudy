package com.github.enteraname74.cloudy.controller.routing

import com.github.enteraname74.cloudy.config.plugin.authenticatedRoutes
import com.github.enteraname74.cloudy.config.plugin.isAdmin
import com.github.enteraname74.cloudy.controller.routing.album.albumRouting
import com.github.enteraname74.cloudy.controller.routing.artist.artistRouting
import com.github.enteraname74.cloudy.controller.routing.auth.authRouting
import com.github.enteraname74.cloudy.controller.routing.music.musicRouting
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        authRouting()
        musicRouting()
        albumRouting()
        artistRouting()
        get("/hello") {
            call.respondText("Hello Ktor My Beloved!")
        }
        authenticatedRoutes {
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