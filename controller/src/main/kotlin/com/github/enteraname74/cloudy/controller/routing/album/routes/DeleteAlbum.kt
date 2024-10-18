package com.github.enteraname74.cloudy.controller.routing.album.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.util.RoutingMessages
import com.github.enteraname74.cloudy.domain.service.AlbumService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*

fun Route.deleteAlbum() {
    val albumService by inject<AlbumService>()

    delete("/{albumId}") {
        val albumId: UUID = try {
            UUID.fromString(call.parameters["albumId"])
        } catch (_: Exception) {
            return@delete badRequest(
                message = RoutingMessages.Generic.WRONG_ID
            )
        }

        val userId: UUID = getUserIdFromToken() ?: return@delete missingTokenInformation()

        val isAlbumPossessedByUser = albumService.isAlbumPossessedByUser(
            albumId = albumId,
            userId = userId,
        )

        if (!isAlbumPossessedByUser) {
            return@delete response(
                status = HttpStatusCode.Forbidden,
                message = RoutingMessages.Album.ALBUM_NOT_POSSESSED_BY_USER,
            )
        }

        val hasBeenDeleted = albumService.deleteAlbum(
            albumId = albumId,
        )

        if (hasBeenDeleted) {
            response(
                status = HttpStatusCode.OK,
                message = RoutingMessages.Album.ALBUM_DELETED,
            )
        } else {
            response(
                status = HttpStatusCode.NotFound,
                message = RoutingMessages.Generic.WRONG_ID,
            )
        }
    }
}