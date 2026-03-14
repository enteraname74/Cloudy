package com.github.enteraname74.cloudy.controller.routing.album.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.forbidden
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.routing.album.model.ModifiedAlbum
import com.github.enteraname74.cloudy.controller.routing.album.model.fromModifiedAlbum
import com.github.enteraname74.cloudy.controller.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.controller.util.MultiPartDataUtils
import com.github.enteraname74.cloudy.domain.model.Album
import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.service.AlbumService
import com.github.enteraname74.cloudy.domain.service.UserService
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import io.ktor.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.updateAlbum() {
    val albumService by inject<AlbumService>()
    val userService by inject<UserService>()

    put {
        val userId: Uuid = getUserIdFromToken() ?: return@put missingTokenInformation()

        val routingMessages: RoutingMessages = getRoutingMessages()
        val user: User = userService.getUserFromId(userId) ?: return@put badRequest(
            message = routingMessages.CANNOT_FIND_USER,
        )

        val multipartData: MultiPartData = call.receiveMultipart()
        when(val updateInformation = MultiPartDataUtils.processUpdateRequest<ModifiedAlbum>(multipartData)) {
            is CloudyResult.Error -> {
                return@put badRequest(routingMessages.WRONG_INFORMATION)
            }
            is CloudyResult.Success -> {
                val modifiedAlbum: ModifiedAlbum = updateInformation.data.second
                val coverData: FileData? = updateInformation.data.first

                val matchingAlbum: Album = albumService.getFromId(albumId = modifiedAlbum.id)
                    ?: return@put badRequest(routingMessages.WRONG_ID)

                val isAlbumPossessedByUser: Boolean = albumService.isAlbumPossessedByUser(
                    albumId = modifiedAlbum.id,
                    userId = userId,
                )
                if (!isAlbumPossessedByUser) {
                    return@put forbidden(routingMessages.ALBUM_NOT_POSSESSED_BY_USER)
                }

                val updatedAlbum: Album = matchingAlbum.fromModifiedAlbum(
                    modifiedAlbum = modifiedAlbum,
                )

                val savedAlbum: Album = albumService.update(
                    modifiedAlbum = updatedAlbum,
                    coverData = coverData,
                    user = user,
                )

                call.respond(savedAlbum)
            }
        }
    }
}