package com.github.enteraname74.cloudy.controller.routing.playlist.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.forbidden
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.routing.playlist.model.ModifiedPlaylist
import com.github.enteraname74.cloudy.controller.routing.playlist.model.fromModifiedPlaylist
import com.github.enteraname74.cloudy.controller.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.model.Playlist
import com.github.enteraname74.cloudy.domain.service.PlaylistService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*

fun Route.updatePlaylist() {
    val playlistService by inject<PlaylistService>()

    put {
        val modifiedPlaylistInfo: ModifiedPlaylist = call.receive()
        val userId: UUID = getUserIdFromToken() ?: return@put missingTokenInformation()

        val routingMessages: RoutingMessages = getRoutingMessages()

        val matchingPlaylist: Playlist = playlistService.getFromId(
            playlistId = modifiedPlaylistInfo.id
        ) ?: return@put badRequest(routingMessages.WRONG_ID)

        val isPlaylistPossessedByUser: Boolean = playlistService.isPlaylistPossessedByUser(
            userId = userId,
            playlistId = modifiedPlaylistInfo.id,
        )

        if (!isPlaylistPossessedByUser) {
            return@put forbidden(routingMessages.PLAYLIST_NOT_POSSESSED_BY_USER)
        }

        val updatedPlaylist: Playlist = matchingPlaylist.fromModifiedPlaylist(modifiedPlaylistInfo)

        val playlist: Playlist = playlistService.upsert(
            playlist = updatedPlaylist,
        )

        call.respond(playlist)
    }
}