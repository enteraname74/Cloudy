package com.github.enteraname74.cloudy.controller.routing.artist.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.config.auth.getUsernameFromToken
import com.github.enteraname74.cloudy.controller.ext.forbidden
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.routing.artist.resource.ArtistResource
import com.github.enteraname74.cloudy.controller.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.service.ArtistService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.deleteArtists() {
    val artistService by inject<ArtistService>()

    delete<ArtistResource> {
        val artistsIds: List<String> = call.receive()
        val uuids: List<Uuid> = artistsIds.mapNotNull { Uuid.parseOrNull(it) }

        val username: String = getUsernameFromToken() ?: return@delete missingTokenInformation()
        val userId: Uuid = getUserIdFromToken() ?: return@delete missingTokenInformation()

        val routingMessages: RoutingMessages = getRoutingMessages()

        uuids.forEach { artistId ->
            val isArtistPossessedByUser = artistService.isArtistPossessedByUser(
                artistId = artistId,
                userId = userId,
            )

            if (!isArtistPossessedByUser) {
                return@delete forbidden(routingMessages.artistNotPossessedByUser(artistId))
            }
        }

        artistService.deleteAll(
            artistIds = uuids,
            username = username,
        )

        response(
            status = HttpStatusCode.OK,
            message = routingMessages.ARTISTS_DELETED,
        )
    }
}