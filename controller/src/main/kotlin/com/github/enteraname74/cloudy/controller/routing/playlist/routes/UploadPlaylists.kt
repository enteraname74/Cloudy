package com.github.enteraname74.cloudy.controller.routing.playlist.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.cannotFindUser
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.respond
import com.github.enteraname74.cloudy.controller.routing.playlist.resource.PlaylistResource
import com.github.enteraname74.cloudy.controller.util.MultiPartDataUtils
import com.github.enteraname74.cloudy.domain.model.playlist.PlaylistUpload
import com.github.enteraname74.cloudy.domain.model.user.User
import com.github.enteraname74.cloudy.domain.service.PlaylistService
import com.github.enteraname74.cloudy.domain.service.UserService
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import io.ktor.http.content.MultiPartData
import io.ktor.server.request.receiveMultipart
import io.ktor.server.resources.post
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.uploadPlaylists() {
    val playlistService by inject<PlaylistService>()
    val userService by inject<UserService>()

    post<PlaylistResource> {

        val userId: Uuid = getUserIdFromToken() ?: return@post missingTokenInformation()
        val user: User = userService.getUserFromId(userId) ?: return@post cannotFindUser()

        val multipartData: MultiPartData = call.receiveMultipart()
        val uploadData = MultiPartDataUtils.processUpdateRequest<PlaylistUpload>(multipartData)

        when (uploadData) {
            is CloudyResult.Error -> respond(uploadData)
            is CloudyResult.Success -> {
                respond(
                    playlistService.upload(
                        playlistUpload = uploadData.data.second,
                        coverData = uploadData.data.first,
                        user = user,
                    )
                )
            }
        }
    }
}