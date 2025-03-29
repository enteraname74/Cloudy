package com.github.enteraname74.cloudy.controller.routing.music.routes

import com.github.enteraname74.cloudy.config.auth.getUsernameFromToken
import com.github.enteraname74.cloudy.controller.ext.*
import com.github.enteraname74.cloudy.controller.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.controller.util.MultiPartDataUtils
import com.github.enteraname74.cloudy.domain.model.CustomMusicMetadata
import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.UploadedMusicData
import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.service.MusicService
import com.github.enteraname74.cloudy.domain.service.UserService
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.uploadMusic() {
    val musicService by inject<MusicService>()
    val userService by inject<UserService>()

    post("/upload") {
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
        val musicFile: CloudyResult<Pair<FileData, CustomMusicMetadata?>> = MultiPartDataUtils.processMusicUploadRequest(
            musicFile = multipartData,
        )

        when (musicFile) {
            is CloudyResult.Error -> {
                return@post badRequest(routingMessages.GIVEN_FILE_IS_NOT_A_MUSIC_FILE)
            }
            is CloudyResult.Success -> {
                val uploadedResult: CloudyResult<UploadedMusicData> = musicService.save(
                    user = user,
                    fileData = musicFile.data.first,
                    customMusicMetadata = musicFile.data.second,
                    shouldSearchForMetadata = shouldSearchForMetadata,
                )

                when(uploadedResult) {
                    is CloudyResult.Error -> {
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