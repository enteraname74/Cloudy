package com.github.enteraname74.cloudy.controller.routing.playlist.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.forbidden
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.util.RoutingMessages
import com.github.enteraname74.cloudy.controller.util.UUIDUtils
import com.github.enteraname74.cloudy.domain.service.PlaylistService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*

fun Route.deletePlaylist() {
    val playlistService by inject<PlaylistService>()

    delete {
        val playlistIds: List<String> = call.receive()
        val uuids: List<UUID> = playlistIds.mapNotNull { UUIDUtils.fromString(it) }

        val userId: UUID = getUserIdFromToken() ?: return@delete missingTokenInformation()

        uuids.forEach { playlistId ->
            val isPossessedByUser = playlistService.isPlaylistPossessedByUser(
                userId = userId,
                playlistId = playlistId,
            )

            if (!isPossessedByUser) {
                return@delete forbidden(RoutingMessages.Playlist.playlistNotPossessedByUser(playlistId))
            }
        }

        playlistService.deleteAll(uuids)

        response(
            status = HttpStatusCode.OK,
            message = RoutingMessages.Playlist.PLAYLISTS_DELETED,
        )
    }
}