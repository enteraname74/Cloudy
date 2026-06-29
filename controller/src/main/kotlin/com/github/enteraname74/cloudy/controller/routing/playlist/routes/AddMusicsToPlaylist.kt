package com.github.enteraname74.cloudy.controller.routing.playlist.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.forbidden
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.service.PlaylistService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.addMusicsToPlaylist() {
    val playlistService by inject<PlaylistService>()

    post("/addMusics/{playlistId}") {
        val routingMessages: RoutingMessages = getRoutingMessages()

        val playlistId: Uuid = Uuid.parseOrNull(
            call.parameters["playlistId"].orEmpty()
        ) ?: return@post badRequest(
            message = routingMessages.WRONG_ID
        )
        val userId: Uuid = getUserIdFromToken() ?: return@post missingTokenInformation()

        if (playlistService.getFromId(playlistId) == null) {
            return@post response(
                status = HttpStatusCode.NotFound,
                message = routingMessages.PLAYLIST_NOT_FOUND,
            )
        }

        val isPossessedByUser = playlistService.isPlaylistPossessedByUser(
            userId = userId,
            playlistId = playlistId,
        )

        if (!isPossessedByUser) {
            return@post forbidden(routingMessages.PLAYLIST_NOT_POSSESSED_BY_USER)
        }

        val musicIds: List<String> = call.receive()
        call.respond(
            playlistService.addToPlaylist(
                playlistId = playlistId,
                userId = userId,
                musicIds = musicIds,
            )
        )
    }
}