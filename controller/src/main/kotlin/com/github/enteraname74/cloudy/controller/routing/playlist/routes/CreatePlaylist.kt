package com.github.enteraname74.cloudy.controller.routing.playlist.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.forbidden
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.routing.playlist.model.PlaylistCreation
import com.github.enteraname74.cloudy.controller.util.RoutingMessages
import com.github.enteraname74.cloudy.domain.model.Playlist
import com.github.enteraname74.cloudy.domain.service.PlaylistService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*

fun Route.createPlaylist() {
    val playlistService by inject<PlaylistService>()

    post {
        val playlistCreation: PlaylistCreation = call.receive()
        val userId: UUID = getUserIdFromToken() ?: return@post missingTokenInformation()

        val isPlaylistPossessedByUser: Boolean = playlistService.isPlaylistPossessedByUser(
            userId = userId,
            playlistName = playlistCreation.name,
        )

        if (isPlaylistPossessedByUser) {
            return@post forbidden(RoutingMessages.Playlist.PLAYLIST_ALREADY_EXISTING)
        }

        val playlist: Playlist = playlistService.upsert(
            playlist = playlistCreation.toNewPlaylist(
                userId = userId,
            ),
        )

        call.respond(playlist)
    }
}