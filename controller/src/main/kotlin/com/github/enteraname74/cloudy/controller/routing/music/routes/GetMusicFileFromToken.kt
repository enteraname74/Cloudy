package com.github.enteraname74.cloudy.controller.routing.music.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromPlayerToken
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.routing.music.resource.MusicResource
import com.github.enteraname74.cloudy.domain.service.MusicService
import io.ktor.http.*
import io.ktor.server.plugins.partialcontent.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.io.File
import kotlin.uuid.Uuid

fun Route.getMusicFileFromToken() {
    val musicService by inject<MusicService>()

    install(PartialContent)

    get<MusicResource.FileFromToken> { musicResource ->
        val userId: Uuid = getUserIdFromPlayerToken(token = musicResource.token) ?: return@get missingTokenInformation()
        val routingMessages = getRoutingMessages()

        val musicFile: File = musicService.getMusicFile(
            musicId = musicResource.musicId,
            userId = userId,
        ) ?: return@get response(
            status = HttpStatusCode.NotFound,
            message = routingMessages.FILE_NOT_FOUND,
        )
        val contentType: ContentType = ContentType.defaultForFile(musicFile)

        call.response.header(
            HttpHeaders.ContentDisposition,
            ContentDisposition.Inline.withParameter(
                ContentDisposition.Parameters.FileName, musicFile.name
            ).toString()
        )
        call.response.header(
            HttpHeaders.ContentType,
            contentType.toString(),
        )
        call.respondFile(musicFile)
    }
}