package com.github.enteraname74.cloudy.controller.routing.music.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.config.auth.getUsernameFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.util.RoutingMessages
import com.github.enteraname74.cloudy.domain.service.MusicFileService
import com.github.enteraname74.cloudy.domain.service.MusicService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*

fun Route.deleteSong() {
    val musicService by inject<MusicService>()
    val musicFileService by inject<MusicFileService>()

    delete("/{musicId}") {
        val musicId: UUID = try {
            UUID.fromString(call.parameters["musicId"])
        } catch (_: Exception) {
            return@delete badRequest(
                message = RoutingMessages.Generic.WRONG_ID
            )
        }

        val username: String = getUsernameFromToken() ?: return@delete missingTokenInformation()
        val userId: UUID = getUserIdFromToken() ?: return@delete missingTokenInformation()

        musicFileService.deleteMusicFile(
            musicId = musicId,
            username = username,
        )
        val hasBeenDeleted = musicService.deleteById(
            musicId = musicId,
            userId = userId,
        )

        if (hasBeenDeleted) {
            response(
                status = HttpStatusCode.OK,
                message = RoutingMessages.Music.SONG_DELETED,
            )
        } else {
            response(
                status = HttpStatusCode.NotFound,
                message = RoutingMessages.Music.FILE_NOT_FOUND,
            )
        }
    }
}