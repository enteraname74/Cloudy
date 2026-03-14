package com.github.enteraname74.cloudy.controller.routing

import com.github.enteraname74.cloudy.config.plugin.authenticatedRoutes
import com.github.enteraname74.cloudy.config.plugin.isAdmin
import com.github.enteraname74.cloudy.controller.routing.album.albumRouting
import com.github.enteraname74.cloudy.controller.routing.artist.artistRouting
import com.github.enteraname74.cloudy.controller.routing.auth.authRouting
import com.github.enteraname74.cloudy.controller.routing.music.musicRouting
import com.github.enteraname74.cloudy.controller.routing.musicartist.musicArtistRouting
import com.github.enteraname74.cloudy.controller.routing.musicplaylist.musicPlaylistRouting
import com.github.enteraname74.cloudy.controller.routing.playlist.playlistRouting
import com.github.enteraname74.cloudy.controller.routing.user.userRoutes
import io.ktor.http.ContentType
import io.ktor.openapi.OpenApiInfo
import io.ktor.server.application.*
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.OpenApiDocSource

fun Application.configureRouting() {
    routing {
        configureSwagger()
        userRoutes()
        authRouting()
        musicRouting()
        albumRouting()
        artistRouting()
        musicArtistRouting()
        musicPlaylistRouting()
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

private fun Route.configureSwagger() {
    swaggerUI("/swagger") {
        info = OpenApiInfo(
            title = "Cloudy Swagger",
            version = "1.0",
        )
        source = OpenApiDocSource.Routing(ContentType.Application.Json) {
            routingRoot.descendants()
        }
    }
}