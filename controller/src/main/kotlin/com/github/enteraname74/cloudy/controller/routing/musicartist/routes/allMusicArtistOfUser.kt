package com.github.enteraname74.cloudy.controller.routing.musicartist.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.getPaginatedRequestFromQueryParam
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.domain.model.MusicArtist
import com.github.enteraname74.cloudy.domain.service.MusicArtistService
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.koin.ktor.ext.inject
import java.util.UUID

fun Route.allMusicArtistOfUser() {
    val musicArtistService by inject<MusicArtistService>()

    get("/ofUser") {
        val userId: UUID = getUserIdFromToken() ?: return@get missingTokenInformation()

        val data: List<MusicArtist> = musicArtistService.allOfUser(
            userId = userId,
            paginatedRequest = getPaginatedRequestFromQueryParam(),
        )

        call.respond(data)
    }
}