package com.github.enteraname74.cloudy.controller.routing.album.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.config.auth.getUsernameFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.forbidden
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.routing.album.resource.AlbumResource
import com.github.enteraname74.cloudy.controller.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.model.album.Album
import com.github.enteraname74.cloudy.domain.service.AlbumService
import com.github.enteraname74.cloudy.domain.service.CoverService
import io.ktor.http.HttpStatusCode
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.getAlbumCover() {
    val coverService by inject<CoverService>()
    val albumService by inject<AlbumService>()

    get<AlbumResource.Cover> { albumResource ->
        val routingMessages: RoutingMessages = getRoutingMessages()

        val username: String = getUsernameFromToken() ?: return@get missingTokenInformation()
        val userId: Uuid = getUserIdFromToken() ?: return@get missingTokenInformation()

        val correspondingArtist: Album = albumService.getFromCoverPath(
            coverPath = "${Album.COVER_PATH}${albumResource.coverId}",
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

        val cover: ByteArray = coverService.getByName(
            name = albumResource.coverId,
            username = username,
        ) ?: return@get badRequest(routingMessages.IMAGE_NOT_FOUND)

        call.respond(cover)
    }
}