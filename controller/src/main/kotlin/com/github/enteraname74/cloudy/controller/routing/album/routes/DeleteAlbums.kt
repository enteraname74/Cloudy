package com.github.enteraname74.cloudy.controller.routing.album.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.config.auth.getUsernameFromToken
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.util.RoutingMessages
import com.github.enteraname74.cloudy.controller.util.UUIDUtils
import com.github.enteraname74.cloudy.domain.service.AlbumService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*

fun Route.deleteAlbums() {
    val albumService by inject<AlbumService>()

    delete {
        val albumIds: List<String> = call.receive()
        val uuids: List<UUID> = albumIds.mapNotNull { UUIDUtils.fromString(it) }

        val username: String = getUsernameFromToken() ?: return@delete missingTokenInformation()
        val userId: UUID = getUserIdFromToken() ?: return@delete missingTokenInformation()

        uuids.forEach { albumId ->
            val isAlbumPossessedByUser = albumService.isAlbumPossessedByUser(
                albumId = albumId,
                userId = userId,
            )

            if (!isAlbumPossessedByUser) {
                return@delete response(
                    status = HttpStatusCode.Forbidden,
                    message = RoutingMessages.Album.albumNotPossessedByUser(albumId),
                )
            }
        }

        albumService.deleteAll(
            albumIds = uuids,
            username = username,
        )

        response(
            status = HttpStatusCode.OK,
            message = RoutingMessages.Album.ALBUMS_DELETED,
        )
    }
}