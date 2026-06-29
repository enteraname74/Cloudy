package com.github.enteraname74.cloudy.controller.routing.playlist.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.config.auth.getUsernameFromToken
import com.github.enteraname74.cloudy.controller.ext.*
import com.github.enteraname74.cloudy.controller.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.model.Playlist
import com.github.enteraname74.cloudy.domain.service.CoverService
import com.github.enteraname74.cloudy.domain.service.PlaylistService
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.getPlaylistCover() {
    val coverService by inject<CoverService>()
    val playlistService by inject<PlaylistService>()

    get("/cover/{coverId}") {
        val routingMessages: RoutingMessages = getRoutingMessages()

        val coverId: String = call.parameters["coverId"] ?: return@get badRequest(
            message = routingMessages.WRONG_ID
        )

        val username: String = getUsernameFromToken() ?: return@get missingTokenInformation()
        val userId: Uuid = getUserIdFromToken() ?: return@get missingTokenInformation()

        val correspondingArtist: Playlist = playlistService.getFromCoverPath(
            coverPath = "${Playlist.COVER_PATH}$coverId",
        ) ?: return@get response(
            status = HttpStatusCode.NotFound,
            message = routingMessages.WRONG_ID,
        )

        val isAlbumPossessedByUser = playlistService.isPlaylistPossessedByUser(
            playlistId = correspondingArtist.id,
            userId = userId
        )

        if (!isAlbumPossessedByUser) {
            return@get forbidden(routingMessages.PLAYLIST_NOT_POSSESSED_BY_USER)
        }

        val cover: ByteArray = coverService.getByName(
            name = coverId,
            username = username,
        ) ?: return@get badRequest(routingMessages.IMAGE_NOT_FOUND)

        call.respond(cover)
    }
}