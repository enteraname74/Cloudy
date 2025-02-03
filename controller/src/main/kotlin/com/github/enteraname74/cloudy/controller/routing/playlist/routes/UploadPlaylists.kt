package com.github.enteraname74.cloudy.controller.routing.playlist.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.routing.playlist.model.PlaylistUpload
import com.github.enteraname74.cloudy.domain.service.PlaylistService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*

fun Route.uploadPlaylists() {
    val playlistService by inject<PlaylistService>()

    post("/upload") {
        val playlists: List<PlaylistUpload> = call.receive()

        val userId: UUID = getUserIdFromToken() ?: return@post missingTokenInformation()

        call.respond(
            playlistService.uploadPlaylists(
                playlists = playlists.map { it.toPlaylist(userId) },
                userId = userId,
            )
        )
    }
}