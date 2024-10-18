package com.github.enteraname74.cloudy.controller.routing.album.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.routing.album.model.ModifiedAlbum
import com.github.enteraname74.cloudy.controller.routing.album.model.fromModifiedAlbum
import com.github.enteraname74.cloudy.controller.util.RoutingMessages
import com.github.enteraname74.cloudy.domain.model.Album
import com.github.enteraname74.cloudy.domain.service.AlbumService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*

fun Route.updateAlbum() {
    val albumService by inject<AlbumService>()

    put {
        val modifiedAlbum: ModifiedAlbum = call.receive()
        val userId: UUID = getUserIdFromToken() ?: return@put missingTokenInformation()

        val matchingAlbum: Album = albumService.getFromId(albumId = modifiedAlbum.id)
            ?: return@put badRequest(RoutingMessages.Generic.WRONG_ID)

        val isAlbumPossessedByUser: Boolean = albumService.isAlbumPossessedByUser(
            albumId = modifiedAlbum.id,
            userId = userId,
        )
        if (!isAlbumPossessedByUser) {
            return@put response(
                status = HttpStatusCode.Forbidden,
                message = RoutingMessages.Album.ALBUM_NOT_POSSESSED_BY_USER,
            )
        }

        val updatedAlbum: Album = matchingAlbum.fromModifiedAlbum(
            modifiedAlbum = modifiedAlbum,
        )

        albumService.upsert(
            modifiedAlbum = updatedAlbum,
            userId = userId,
        )

        response(
            status = HttpStatusCode.OK,
            message = RoutingMessages.Album.ALBUM_UPDATED,
        )
    }
}