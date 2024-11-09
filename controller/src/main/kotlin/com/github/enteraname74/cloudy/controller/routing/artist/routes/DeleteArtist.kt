package com.github.enteraname74.cloudy.controller.routing.artist.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.config.auth.getUsernameFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.util.RoutingMessages
import com.github.enteraname74.cloudy.domain.service.ArtistService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import org.koin.ktor.ext.inject
import java.util.UUID

fun Route.deleteArtist() {
    val artistService by inject<ArtistService>()

    delete("/{artistId}") {
        val artistId: UUID = try {
            UUID.fromString(call.parameters["artistId"])
        } catch (_: Exception) {
            return@delete badRequest(
                message = RoutingMessages.Generic.WRONG_ID
            )
        }

        val username: String = getUsernameFromToken() ?: return@delete missingTokenInformation()
        val userId: UUID = getUserIdFromToken() ?: return@delete missingTokenInformation()

        val isArtistPossessedByUser = artistService.isArtistPossessedByUser(
            artistId = artistId,
            userId = userId,
        )

        if (!isArtistPossessedByUser) {
            return@delete response(
                status = HttpStatusCode.Forbidden,
                message = RoutingMessages.Artist.ARTIST_NOT_POSSESSED_BY_USER,
            )
        }

        val hasBeenDeleted = artistService.deleteArtist(
            artistId = artistId,
            username = username,
        )

        if (hasBeenDeleted) {
            response(
                status = HttpStatusCode.OK,
                message = RoutingMessages.Artist.ARTIST_DELETED,
            )
        } else {
            response(
                status = HttpStatusCode.NotFound,
                message = RoutingMessages.Generic.WRONG_ID,
            )
        }
    }
}