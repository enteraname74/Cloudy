package com.github.enteraname74.cloudy.controller.routing.music.routes

import com.github.enteraname74.cloudy.config.auth.getUsernameFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.cannotFindUser
import com.github.enteraname74.cloudy.controller.ext.forbidden
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.routing.music.resource.MusicResource
import com.github.enteraname74.cloudy.controller.util.MultiPartDataUtils
import com.github.enteraname74.cloudy.domain.model.music.Music
import com.github.enteraname74.cloudy.domain.model.music.MusicUploadPayload
import com.github.enteraname74.cloudy.domain.model.user.User
import com.github.enteraname74.cloudy.domain.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.service.MusicService
import com.github.enteraname74.cloudy.domain.service.UserService
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.logging.cloudyLogger
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.MultiPartData
import io.ktor.server.request.header
import io.ktor.server.request.receiveMultipart
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject

fun Route.uploadMusic() {
    val musicService by inject<MusicService>()
    val userService by inject<UserService>()

    post<MusicResource.Upload> {
        val multipartData: MultiPartData = call.receiveMultipart()

        val routingMessages: RoutingMessages = getRoutingMessages()

        val contentLength = call.request.header(HttpHeaders.ContentLength)?.toLong()
            ?: return@post badRequest(message = routingMessages.NO_FILE_DATA)

        val username: String = getUsernameFromToken() ?: return@post missingTokenInformation()

        if (userService.isUserDirectoryFull(username)) {
            return@post forbidden(routingMessages.USER_MAX_STORAGE_REACHED)
        }

        if (userService.isUserDirectoryFull(
                username = username,
                addedSize = contentLength,
            )
        ) return@post response(
            status = HttpStatusCode.PayloadTooLarge,
            message = routingMessages.FILE_TOO_HEAVY,
        )

        val user: User = userService.getUserFromUsername(
            username = username
        ) ?: return@post cannotFindUser()

        val shouldSearchForMetadata: Boolean = call.request.queryParameters["searchMetadata"]?.toBoolean() == true
        val payload: CloudyResult<MusicUploadPayload> = MultiPartDataUtils.processMusicUploadRequest(
            request = multipartData,
        )

        when (payload) {
            is CloudyResult.Error -> {
                cloudyLogger.error("Error while retrieving upload song payload: $payload")
                return@post badRequest(routingMessages.GIVEN_FILE_IS_NOT_A_MUSIC_FILE)
            }
            is CloudyResult.Success -> {
                val uploadedResult: CloudyResult<Music> = musicService.saveUserFile(
                    user = user,
                    shouldSearchForMetadata = shouldSearchForMetadata,
                    payload = payload.data,
                )

                when (uploadedResult) {
                    is CloudyResult.Error -> {
                        cloudyLogger.error("Error while uploading song: $uploadedResult")
                        return@post badRequest(routingMessages.CANNOT_SAVE_SONG)
                    }
                    is CloudyResult.Success -> {
                        call.respond(uploadedResult.data)
                    }
                }
            }
        }
    }
}