package com.github.enteraname74.cloudy.controller.routing.playlist.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.forbidden
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.routing.playlist.resource.PlaylistResource
import com.github.enteraname74.cloudy.domain.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.service.PlaylistService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.resources.delete
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.deletePlaylist() {
    val playlistService by inject<PlaylistService>()

    delete<PlaylistResource> {
        val playlistIds: List<String> = call.receive()
        val uuids: List<Uuid> = playlistIds.mapNotNull { Uuid.parseOrNull(it) }

        val userId: Uuid = getUserIdFromToken() ?: return@delete missingTokenInformation()

        val routingMessages: RoutingMessages = getRoutingMessages()

        uuids.forEach { playlistId ->
            val isPossessedByUser = playlistService.isPlaylistPossessedByUser(
                userId = userId,
                playlistId = playlistId,
            )

            if (!isPossessedByUser) {
                return@delete forbidden(routingMessages.playlistNotPossessedByUser(playlistId))
            }
        }

        playlistService.deleteAll(uuids)

        response(
            status = HttpStatusCode.OK,
            message = routingMessages.PLAYLISTS_DELETED,
        )
    }
}