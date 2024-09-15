package com.github.enteraname74.cloudy.controller.routing.artist.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.domain.service.ArtistService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.UUID

fun Route.artistsOfUser() {

    val artistService by inject<ArtistService>()

    get("/ofUser") {
        val userId: UUID = getUserIdFromToken() ?: return@get missingTokenInformation()
        call.respond(message = artistService.getAllOfUser(userId))
    }
}