package com.github.enteraname74.cloudy.controller.routing.artist.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.config.auth.getUsernameFromToken
import com.github.enteraname74.cloudy.controller.ext.*
import com.github.enteraname74.cloudy.controller.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.model.artist.Artist
import com.github.enteraname74.cloudy.domain.service.ArtistService
import com.github.enteraname74.cloudy.domain.service.CoverService
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.getArtistCover() {
    val coverService by inject<CoverService>()
    val artistService by inject<ArtistService>()

    get("/cover/{coverId}") {
        val routingMessages: RoutingMessages = getRoutingMessages()

        val coverId: String = call.parameters["coverId"] ?: return@get badRequest(
            message = routingMessages.WRONG_ID
        )

        val username: String = getUsernameFromToken() ?: return@get missingTokenInformation()
        val userId: Uuid = getUserIdFromToken() ?: return@get missingTokenInformation()

        val correspondingArtist: Artist = artistService.getFromCoverPath(
            coverPath = "${Artist.COVER_PATH}$coverId",
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
            name = coverId,
            username = username,
        ) ?: return@get badRequest(routingMessages.IMAGE_NOT_FOUND)

        call.respond(cover)
    }
}