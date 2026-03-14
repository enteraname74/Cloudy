package com.github.enteraname74.cloudy.controller.routing.musicartist.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.getPaginatedRequestFromQueryParam
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.domain.model.MusicArtist
import com.github.enteraname74.cloudy.domain.service.MusicArtistService
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.allMusicArtistOfUser() {
    val musicArtistService by inject<MusicArtistService>()

    get("/ofUser") {
        val userId: Uuid = getUserIdFromToken() ?: return@get missingTokenInformation()

        val data: List<MusicArtist> = musicArtistService.allOfUser(
            userId = userId,
            paginatedRequest = getPaginatedRequestFromQueryParam(),
        )

        call.respond(data)
    }
}