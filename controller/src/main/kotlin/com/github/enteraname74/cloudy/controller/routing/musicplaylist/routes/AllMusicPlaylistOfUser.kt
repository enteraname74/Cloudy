package com.github.enteraname74.cloudy.controller.routing.musicplaylist.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.getPaginatedRequestFromQueryParam
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.domain.service.MusicPlaylistService
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.allMusicPlaylistOfUser() {
    val musicPlaylistService by inject<MusicPlaylistService>()

    get("/ofUser") {
        val userId: Uuid = getUserIdFromToken() ?: return@get missingTokenInformation()

        call.respond(
            musicPlaylistService.allOfUser(
                userId = userId,
                paginatedRequest = getPaginatedRequestFromQueryParam(),
            )
        )
    }

}