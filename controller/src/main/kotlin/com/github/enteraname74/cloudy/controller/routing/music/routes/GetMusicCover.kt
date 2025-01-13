package com.github.enteraname74.cloudy.controller.routing.music.routes

import com.github.enteraname74.cloudy.config.auth.getUsernameFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.util.RoutingMessages
import com.github.enteraname74.cloudy.controller.util.UUIDUtils
import com.github.enteraname74.cloudy.domain.service.CoverService
import com.github.enteraname74.cloudy.domain.service.MusicFileService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.koin.ktor.ext.inject
import java.io.File
import java.util.UUID
import kotlin.getValue

fun Route.getMusicCover() {
    val musicFileService by inject<MusicFileService>()
    val coverService by inject<CoverService>()

    get("/cover/{musicId}") {
        val musicId: UUID = UUIDUtils.fromString(
            call.parameters["musicId"]
        ) ?: return@get badRequest(
            message = RoutingMessages.Generic.WRONG_ID
        )

        val username: String = getUsernameFromToken() ?: return@get missingTokenInformation()

        val musicFile: File = musicFileService.getMusicFile(
            musicId = musicId,
            username = username,
        ) ?: return@get response(
            status = HttpStatusCode.NotFound,
            message = RoutingMessages.Music.FILE_NOT_FOUND,
        )

        val musicFileCover: ByteArray = coverService.getMusicFileCover(
            file = musicFile
        ) ?: byteArrayOf()

        call.respondBytes(musicFileCover)
    }
}