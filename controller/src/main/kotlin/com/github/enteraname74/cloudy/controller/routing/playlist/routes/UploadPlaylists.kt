package com.github.enteraname74.cloudy.controller.routing.playlist.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.cannotFindUser
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.routing.playlist.model.UploadPlaylistBody
import com.github.enteraname74.cloudy.controller.routing.playlist.resource.PlaylistResource
import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.model.playlist.PlaylistUpload
import com.github.enteraname74.cloudy.domain.service.PlaylistService
import com.github.enteraname74.cloudy.domain.service.UserService
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.uploadPlaylists() {
    val playlistService by inject<PlaylistService>()
    val userService by inject<UserService>()

    post<PlaylistResource> {
        val playlistUpload: PlaylistUpload = call.receive()

        val userId: Uuid = getUserIdFromToken() ?: return@post missingTokenInformation()
        val user: User = userService.getUserFromId(userId) ?: return@post cannotFindUser()


        val result = playlistService.upload(
            playlistUpload = playlistUpload,
            user = user,
        )

        when (result) {
            is CloudyResult.Error -> badRequest(getRoutingMessages().CANNOT_SAVE_PLAYLIST)
            is CloudyResult.Success -> call.respond(result.data)
        }
    }
}