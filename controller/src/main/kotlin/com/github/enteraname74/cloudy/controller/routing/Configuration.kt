package com.github.enteraname74.cloudy.controller.routing

import com.github.enteraname74.cloudy.controller.routing.album.albumRouting
import com.github.enteraname74.cloudy.controller.routing.artist.artistRouting
import com.github.enteraname74.cloudy.controller.routing.auth.authRouting
import com.github.enteraname74.cloudy.controller.routing.music.musicRouting
import com.github.enteraname74.cloudy.controller.routing.player.playerRouting
import com.github.enteraname74.cloudy.controller.routing.playlist.playlistRouting
import com.github.enteraname74.cloudy.controller.routing.user.userRoutes
import io.ktor.http.ContentType
import io.ktor.openapi.OpenApiInfo
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.openapi.OpenApiDocSource
import io.ktor.server.routing.routing
import io.ktor.server.routing.routingRoot

fun Application.configureRouting() {
    install(DefaultHeaders) {
        header("Cross-Origin-Resource-Policy", "cross-origin")
    }
    routing {
        configureSwagger()
        userRoutes()
        authRouting()
        musicRouting()
        albumRouting()
        artistRouting()
        playlistRouting()
        playerRouting()
        get("/hello") {
            call.respondText("Hello Ktor My Beloved!")
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