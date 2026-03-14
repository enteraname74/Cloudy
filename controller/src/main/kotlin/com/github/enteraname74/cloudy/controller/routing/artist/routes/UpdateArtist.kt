package com.github.enteraname74.cloudy.controller.routing.artist.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.forbidden
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.routing.artist.model.ModifiedArtist
import com.github.enteraname74.cloudy.controller.routing.artist.model.fromModifiedArtist
import com.github.enteraname74.cloudy.controller.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.controller.util.MultiPartDataUtils
import com.github.enteraname74.cloudy.domain.model.Artist
import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.service.ArtistService
import com.github.enteraname74.cloudy.domain.service.UserService
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import io.ktor.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.updateArtist() {

    val artistService by inject<ArtistService>()
    val userService by inject<UserService>()

    put {
        val userId: Uuid = getUserIdFromToken() ?: return@put missingTokenInformation()

        val routingMessages: RoutingMessages = getRoutingMessages()
        val user: User = userService.getUserFromId(userId) ?: return@put badRequest(
            message = routingMessages.CANNOT_FIND_USER,
        )

        val multipartData: MultiPartData = call.receiveMultipart()
        when(val updateInformation = MultiPartDataUtils.processUpdateRequest<ModifiedArtist>(multipartData)) {
            is CloudyResult.Error -> {
                return@put badRequest(routingMessages.WRONG_INFORMATION)
            }
            is CloudyResult.Success -> {
                val modifiedArtist: ModifiedArtist = updateInformation.data.second
                val coverData: FileData? = updateInformation.data.first

                val matchingArtist: Artist = artistService.getFromId(artistId = modifiedArtist.id)
                    ?: return@put badRequest(routingMessages.WRONG_ID)

                val isArtistPossessedByUser: Boolean = artistService.isArtistPossessedByUser(
                    artistId = modifiedArtist.id,
                    userId = userId,
                )
                if (!isArtistPossessedByUser) {
                    return@put forbidden(routingMessages.ARTIST_NOT_POSSESSED_BY_USER)
                }

                val updatedArtist: Artist = matchingArtist.fromModifiedArtist(
                    modifiedArtist = modifiedArtist,
                )

                val artist: Artist = artistService.update(
                    modifiedArtist = updatedArtist,
                    user = user,
                    coverData = coverData,
                )

                call.respond(artist)
            }
        }
    }
}