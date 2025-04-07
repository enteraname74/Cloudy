package com.github.enteraname74.cloudy.controller.routing.album.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.config.auth.getUsernameFromToken
import com.github.enteraname74.cloudy.controller.ext.*
import com.github.enteraname74.cloudy.controller.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.controller.util.UUIDUtils
import com.github.enteraname74.cloudy.domain.model.Album
import com.github.enteraname74.cloudy.domain.service.AlbumService
import com.github.enteraname74.cloudy.domain.service.CoverService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*

fun Route.getAlbumCover() {
    val coverService by inject<CoverService>()
    val albumService by inject<AlbumService>()

    get("/cover/{coverId}") {
        val routingMessages: RoutingMessages = getRoutingMessages()

        val coverId: UUID = UUIDUtils.fromString(
            call.parameters["coverId"]
        ) ?: return@get badRequest(
            message = routingMessages.WRONG_ID
        )

        val username: String = getUsernameFromToken() ?: return@get missingTokenInformation()
        val userId: UUID = getUserIdFromToken() ?: return@get missingTokenInformation()

        val correspondingArtist: Album = albumService.getFromCoverPath(
            coverPath = "${Album.COVER_PATH}$coverId",
        ) ?: return@get response(
            status = HttpStatusCode.NotFound,
            message = routingMessages.WRONG_ID,
        )

        val isAlbumPossessedByUser = albumService.isAlbumPossessedByUser(
            albumId = correspondingArtist.id,
            userId = userId
        )

        if (!isAlbumPossessedByUser) {
            return@get forbidden(routingMessages.ALBUM_NOT_POSSESSED_BY_USER)
        }

        val cover: ByteArray = coverService.getById(
            id = coverId,
            username = username,
        ) ?: return@get badRequest(routingMessages.IMAGE_NOT_FOUND)

        call.respond(cover)
    }
}