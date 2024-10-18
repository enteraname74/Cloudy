package com.github.enteraname74.cloudy.controller.routing.artist.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.routing.artist.model.ModifiedArtist
import com.github.enteraname74.cloudy.controller.routing.artist.model.fromModifiedArtist
import com.github.enteraname74.cloudy.controller.util.RoutingMessages
import com.github.enteraname74.cloudy.domain.model.Artist
import com.github.enteraname74.cloudy.domain.service.ArtistService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.UUID

fun Route.updateArtist() {

    val artistService by inject<ArtistService>()

    put {
        val modifiedArtist: ModifiedArtist = call.receive()
        val userId: UUID = getUserIdFromToken() ?: return@put missingTokenInformation()

        val matchingArtist: Artist = artistService.getFromId(artistId = modifiedArtist.id)
            ?: return@put badRequest(RoutingMessages.Generic.WRONG_ID)

        val isArtistPossessedByUser: Boolean = artistService.isArtistPossessedByUser(
            artistId = modifiedArtist.id,
            userId = userId,
        )
        if (!isArtistPossessedByUser) {
            return@put response(
                status = HttpStatusCode.Forbidden,
                message = RoutingMessages.Artist.ARTIST_NOT_POSSESSED_BY_USER,
            )
        }

        val updatedArtist: Artist = matchingArtist.fromModifiedArtist(
            modifiedArtist = modifiedArtist,
        )

        artistService.upsert(
            modifiedArtist = updatedArtist,
            userId = userId,
        )

        response(
            status = HttpStatusCode.OK,
            message = RoutingMessages.Artist.ARTIST_UPDATED,
        )
    }
}