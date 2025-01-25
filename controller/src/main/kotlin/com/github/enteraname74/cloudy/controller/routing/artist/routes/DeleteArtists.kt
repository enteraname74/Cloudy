package com.github.enteraname74.cloudy.controller.routing.artist.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.config.auth.getUsernameFromToken
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.util.RoutingMessages
import com.github.enteraname74.cloudy.controller.util.UUIDUtils
import com.github.enteraname74.cloudy.domain.service.ArtistService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*

fun Route.deleteArtists() {
    val artistService by inject<ArtistService>()

    delete("/{artistId}") {
        val artistsIds: List<String> = call.receive()
        val uuids: List<UUID> = artistsIds.mapNotNull { UUIDUtils.fromString(it) }

        val username: String = getUsernameFromToken() ?: return@delete missingTokenInformation()
        val userId: UUID = getUserIdFromToken() ?: return@delete missingTokenInformation()

        uuids.forEach { artistId ->
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
        }

        artistService.deleteAll(
            artistIds = uuids,
            username = username,
        )

        response(
            status = HttpStatusCode.OK,
            message = RoutingMessages.Artist.ARTISTS_DELETED,
        )
    }
}