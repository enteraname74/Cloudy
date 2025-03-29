package com.github.enteraname74.cloudy.controller.routing.music.routes

import com.github.enteraname74.cloudy.config.auth.getUsernameFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.controller.util.UUIDUtils
import com.github.enteraname74.cloudy.domain.service.CoverService
import com.github.enteraname74.cloudy.domain.service.MusicService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.io.File
import java.util.*

fun Route.getMusicCover() {
    val musicService by inject<MusicService>()
    val coverService by inject<CoverService>()

    get("/cover/{musicId}") {
        val routingMessages: RoutingMessages = getRoutingMessages()

        val musicId: UUID = UUIDUtils.fromString(
            call.parameters["musicId"]
        ) ?: return@get badRequest(
            message = routingMessages.WRONG_ID
        )

        val username: String = getUsernameFromToken() ?: return@get missingTokenInformation()

        val musicFile: File = musicService.getMusicFile(
            musicId = musicId,
            username = username,
        ) ?: return@get response(
            status = HttpStatusCode.NotFound,
            message = routingMessages.FILE_NOT_FOUND,
        )

        /*
        We first try to retrieve a custom cover for the file, else, we fetch it from its file.
         */
        val foundCover: ByteArray? = coverService.getById(
            id = musicId,
            username = username,
        )

        val musicFileCover: ByteArray? = foundCover ?: coverService.getMusicFileCover(
            file = musicFile
        )

        call.respondBytes(musicFileCover ?: byteArrayOf())
    }
}