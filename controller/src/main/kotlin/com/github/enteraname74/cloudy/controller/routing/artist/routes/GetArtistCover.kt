package com.github.enteraname74.cloudy.controller.routing.artist.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.config.auth.getUsernameFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.forbidden
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.routing.artist.resource.ArtistResource
import com.github.enteraname74.cloudy.controller.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.model.artist.Artist
import com.github.enteraname74.cloudy.domain.service.ArtistService
import com.github.enteraname74.cloudy.domain.service.CoverService
import io.ktor.http.HttpStatusCode
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.getArtistCover() {
    val coverService by inject<CoverService>()
    val artistService by inject<ArtistService>()

    get<ArtistResource.Cover> { artistResource ->
        val routingMessages: RoutingMessages = getRoutingMessages()

        val username: String = getUsernameFromToken() ?: return@get missingTokenInformation()
        val userId: Uuid = getUserIdFromToken() ?: return@get missingTokenInformation()

        val correspondingArtist: Artist = artistService.getFromCoverPath(
            coverPath = "${Artist.COVER_PATH}${artistResource.coverId}",
        ) ?: return@get response(
            status = HttpStatusCode.NotFound,
            message = routingMessages.WRONG_ID,
        )

        val isArtistPossessedByUser = artistService.isArtistPossessedByUser(
            artistId = correspondingArtist.id,
            userId = userId
        )

        if (!isArtistPossessedByUser) {
            return@get forbidden(routingMessages.ARTIST_NOT_POSSESSED_BY_USER)
        }

        val cover: ByteArray = coverService.getByName(
            name = artistResource.coverId,
            username = username,
        ) ?: return@get badRequest(routingMessages.IMAGE_NOT_FOUND)

        call.respond(cover)
    }
}