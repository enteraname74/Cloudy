package com.github.enteraname74.cloudy.controller.routing.playlist.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.routing.playlist.model.ModifiedPlaylist
import com.github.enteraname74.cloudy.controller.routing.playlist.model.fromModifiedPlaylist
import com.github.enteraname74.cloudy.controller.util.RoutingMessages
import com.github.enteraname74.cloudy.domain.model.Playlist
import com.github.enteraname74.cloudy.domain.service.PlaylistService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.put
import org.koin.ktor.ext.inject
import java.util.UUID

fun Route.updatePlaylist() {
    val playlistService by inject<PlaylistService>()

    put {
        val modifiedPlaylistInfo: ModifiedPlaylist = call.receive()
        val userId: UUID = getUserIdFromToken() ?: return@put missingTokenInformation()

        val matchingPlaylist: Playlist = playlistService.getFromId(
            playlistId = modifiedPlaylistInfo.id
        ) ?: return@put badRequest(RoutingMessages.Generic.WRONG_ID)

        val isPlaylistPossessedByUser: Boolean = playlistService.isPlaylistPossessedByUser(
            userId = userId,
            playlistId = modifiedPlaylistInfo.id,
        )

        if (!isPlaylistPossessedByUser) {
            return@put response(
                status = HttpStatusCode.Forbidden,
                message = RoutingMessages.Playlist.PLAYLIST_NOT_POSSESSED_BY_USER,
            )
        }

        val updatedPlaylist: Playlist = matchingPlaylist.fromModifiedPlaylist(modifiedPlaylistInfo)

        val playlist: Playlist = playlistService.update(
            playlist = updatedPlaylist,
        )

        call.respond(playlist)
    }
}