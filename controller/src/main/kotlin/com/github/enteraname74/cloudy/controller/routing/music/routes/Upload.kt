package com.github.enteraname74.cloudy.controller.routing.music.routes

import com.github.enteraname74.cloudy.config.auth.getUsernameFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.cannotFindUser
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.util.RoutingMessages
import com.github.enteraname74.cloudy.controller.util.ServerUtil
import com.github.enteraname74.cloudy.domain.filepersistence.MusicInformationResult
import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.service.MusicFileService
import com.github.enteraname74.cloudy.domain.service.MusicService
import com.github.enteraname74.cloudy.domain.service.UserService
import com.github.enteraname74.cloudy.domain.util.ServiceResult
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.upload() {

    val musicFileService by inject<MusicFileService>()
    val musicService by inject<MusicService>()
    val userService by inject<UserService>()

    post("/upload") {
        val multipartData: MultiPartData = call.receiveMultipart()
        val contentLength = call.request.header(HttpHeaders.ContentLength)?.toLong()
            ?: return@post badRequest(message = RoutingMessages.Music.NO_FILE_DATA)

        val username: String = getUsernameFromToken() ?: return@post missingTokenInformation()

        if (musicFileService.isUserDirectoryFull(username)) return@post response(
            status = HttpStatusCode.Forbidden,
            message = RoutingMessages.Music.USER_MAX_STORAGE_REACHED,
        )

        if (musicFileService.isUserDirectoryFull(
                username = username,
                addedSize = contentLength,
            )
        ) return@post response(
            status = HttpStatusCode.PayloadTooLarge,
            message = RoutingMessages.Music.FILE_TOO_HEAVY,
        )

        val user: User = userService.getUserFromUsername(
            username = username
        ) ?: return@post cannotFindUser()

        val shouldSearchForMetadata: Boolean = call.request.queryParameters["searchMetadata"]?.toBoolean() == true

        val serviceResult: ServiceResult = musicFileService.save(
            user = user,
            file = multipartData,
            shouldSearchForMetadata = shouldSearchForMetadata,
        )

        when(serviceResult) {
            is ServiceResult.Error -> {
                badRequest(message = serviceResult.message.orEmpty())
            }
            is ServiceResult.Ok -> {
                val musicInformationResult = serviceResult.data as MusicInformationResult.FileMetadata
                val path: String = ServerUtil.buildRoute(value = "music/${musicInformationResult.musicId}")

                musicService.saveAndCreateMissingAlbumAndArtist(
                    user = user,
                    musicInformationResult = musicInformationResult,
                    musicPath = path,
                )
                response(
                    status = HttpStatusCode.Accepted,
                    message = RoutingMessages.Music.FILE_SAVED
                )
            }
        }
    }
}