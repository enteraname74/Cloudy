package com.github.enteraname74.cloudy.controller.routing.playlist.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.config.auth.getUsernameFromToken
import com.github.enteraname74.cloudy.controller.ext.forbidden
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.routing.playlist.model.PlaylistCreation
import com.github.enteraname74.cloudy.domain.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.model.playlist.Playlist
import com.github.enteraname74.cloudy.domain.service.PlaylistService
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.createPlaylist() {
    val playlistService by inject<PlaylistService>()

    post {
        val playlistCreation: PlaylistCreation = call.receive()
        val userId: Uuid = getUserIdFromToken() ?: return@post missingTokenInformation()
        val username: String = getUsernameFromToken() ?: return@post missingTokenInformation()

        val isPlaylistPossessedByUser: Boolean = playlistService.isPlaylistPossessedByUser(
            userId = userId,
            playlistName = playlistCreation.name,
        )

        val routingMessages: RoutingMessages = getRoutingMessages()

        if (isPlaylistPossessedByUser) {
            return@post forbidden(routingMessages.PLAYLIST_ALREADY_EXISTING)
        }

        // TODO: Implement cover support when creating a playlist.
        val playlist: Playlist = playlistService.upsert(
            playlist = playlistCreation.toNewPlaylist(
                userId = userId,
            ),
            coverData = null,
            username = username,
        )

        call.respond(playlist)
    }
}