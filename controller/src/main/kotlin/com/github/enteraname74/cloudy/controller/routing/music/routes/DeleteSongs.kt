package com.github.enteraname74.cloudy.controller.routing.music.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.config.auth.getUsernameFromToken
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.util.RoutingMessages
import com.github.enteraname74.cloudy.controller.util.UUIDUtils
import com.github.enteraname74.cloudy.domain.service.MusicFileService
import com.github.enteraname74.cloudy.domain.service.MusicService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*

fun Route.deleteSongs() {
    val musicService by inject<MusicService>()
    val musicFileService by inject<MusicFileService>()

    delete {
        val musicIds: List<String> = call.receive()
        val uuids: List<UUID> = musicIds.mapNotNull { UUIDUtils.fromString(it) }

        val username: String = getUsernameFromToken() ?: return@delete missingTokenInformation()
        val userId: UUID = getUserIdFromToken() ?: return@delete missingTokenInformation()

        uuids.forEach { musicId ->
            val isPossessedByUser = musicService.isMusicPossessedByUser(
                musicId = musicId,
                userId = userId,
            )

            if (!isPossessedByUser) {
                return@delete response(
                    status = HttpStatusCode.Forbidden,
                    message = RoutingMessages.Music.songNotPossessedByUser(musicId),
                )
            }
        }

        uuids.forEach { musicId ->
            musicFileService.deleteMusicFile(
                musicId = musicId,
                username = username,
            )
        }

        musicService.deleteAll(
            musicIds = uuids,
        )

        response(
            status = HttpStatusCode.OK,
            message = RoutingMessages.Music.SONGS_DELETED,
        )
    }
}