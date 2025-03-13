package com.github.enteraname74.cloudy.controller.routing.music.routes

import com.github.enteraname74.cloudy.config.auth.getUsernameFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.controller.util.UUIDUtils
import com.github.enteraname74.cloudy.domain.service.MusicFileService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.io.File
import java.util.*

fun Route.getMusicFile() {
    val musicFileService by inject<MusicFileService>()

    get("/{musicId}") {
        val routingMessages: RoutingMessages = getRoutingMessages()

        val musicId: UUID = UUIDUtils.fromString(
            call.parameters["musicId"]
        ) ?: return@get badRequest(
            message = routingMessages.WRONG_ID
        )

        val username: String = getUsernameFromToken() ?: return@get missingTokenInformation()

        val musicFile: File = musicFileService.getMusicFile(
            musicId = musicId,
            username = username,
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