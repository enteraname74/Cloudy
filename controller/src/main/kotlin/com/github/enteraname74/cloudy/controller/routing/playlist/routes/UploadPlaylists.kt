package com.github.enteraname74.cloudy.controller.routing.playlist.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.cannotFindUser
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.routing.playlist.model.UploadPlaylistBody
import com.github.enteraname74.cloudy.controller.routing.playlist.resource.PlaylistResource
import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.service.PlaylistService
import com.github.enteraname74.cloudy.domain.service.UserService
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
        val body: UploadPlaylistBody = call.receive()

        val userId: Uuid = getUserIdFromToken() ?: return@post missingTokenInformation()
        val user: User = userService.getUserFromId(userId) ?: return@post cannotFindUser()


        playlistService.upload(
            playlists = body.playlists,
            user = user,
        )

        call.respond(HttpStatusCode.Accepted)
    }
}