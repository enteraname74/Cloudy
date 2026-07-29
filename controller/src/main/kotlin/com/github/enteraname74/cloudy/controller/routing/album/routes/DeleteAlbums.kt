package com.github.enteraname74.cloudy.controller.routing.album.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.config.auth.getUsernameFromToken
import com.github.enteraname74.cloudy.controller.ext.forbidden
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.routing.album.resource.AlbumResource
import com.github.enteraname74.cloudy.domain.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.service.AlbumService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.deleteAlbums() {
    val albumService by inject<AlbumService>()

    delete<AlbumResource> {
        val albumIds: List<String> = call.receive()
        val uuids: List<Uuid> = albumIds.mapNotNull { Uuid.parseOrNull(it) }

        val username: String = getUsernameFromToken() ?: return@delete missingTokenInformation()
        val userId: Uuid = getUserIdFromToken() ?: return@delete missingTokenInformation()

        val routingMessages: RoutingMessages = getRoutingMessages()

        uuids.forEach { albumId ->
            val isAlbumPossessedByUser = albumService.isAlbumPossessedByUser(
                albumId = albumId,
                userId = userId,
            )

            if (!isAlbumPossessedByUser) {
                return@delete forbidden(routingMessages.albumNotPossessedByUser(albumId))
            }
        }

        albumService.deleteAll(
            albumIds = uuids,
            username = username,
        )

        response(
            status = HttpStatusCode.OK,
            message = routingMessages.ALBUMS_DELETED,
        )
    }
}