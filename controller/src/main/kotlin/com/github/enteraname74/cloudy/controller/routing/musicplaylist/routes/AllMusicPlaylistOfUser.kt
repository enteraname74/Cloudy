package com.github.enteraname74.cloudy.controller.routing.musicplaylist.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.getPaginatedRequestFromQueryParam
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.domain.service.MusicPlaylistService
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.koin.ktor.ext.inject
import java.util.UUID

fun Route.allMusicPlaylistOfUser() {
    val musicPlaylistService by inject<MusicPlaylistService>()

    get("/ofUser") {
        val userId: UUID = getUserIdFromToken() ?: return@get missingTokenInformation()

        call.respond(
            musicPlaylistService.allOfUser(
                userId = userId,
                paginatedRequest = getPaginatedRequestFromQueryParam(),
            )
        )
    }

}