package com.github.enteraname74.cloudy.controller.routing.playlist.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.config.auth.getUsernameFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.forbidden
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.routing.playlist.resource.PlaylistResource
import com.github.enteraname74.cloudy.domain.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.model.playlist.Playlist
import com.github.enteraname74.cloudy.domain.service.CoverService
import com.github.enteraname74.cloudy.domain.service.PlaylistService
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.resources.get
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.getPlaylistCover() {
    val coverService by inject<CoverService>()
    val playlistService by inject<PlaylistService>()

    get<PlaylistResource.Cover> { playlistResource ->
        val routingMessages: RoutingMessages = getRoutingMessages()

        val username: String = getUsernameFromToken() ?: return@get missingTokenInformation()
        val userId: Uuid = getUserIdFromToken() ?: return@get missingTokenInformation()

        val correspondingPlaylist: Playlist = playlistService.getFromCoverPath(
            coverPath = "${Playlist.COVER_PATH}${playlistResource.coverId}",
        ) ?: return@get response(
            status = HttpStatusCode.NotFound,
            message = routingMessages.WRONG_ID,
        )

        val isPlaylistPossessed = playlistService.isPlaylistPossessedByUser(
            playlistId = correspondingPlaylist.id,
            userId = userId
        )

        if (!isPlaylistPossessed) {
            return@get forbidden(routingMessages.PLAYLIST_NOT_POSSESSED_BY_USER)
        }

        val cover: ByteArray = coverService.getByName(
            name = playlistResource.coverId,
            username = username,
        ) ?: return@get badRequest(routingMessages.IMAGE_NOT_FOUND)

        call.respond(cover)
    }
}