package com.github.enteraname74.cloudy.controller.routing.music.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.config.auth.getUsernameFromToken
import com.github.enteraname74.cloudy.controller.ext.forbidden
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.controller.util.UUIDUtils
import com.github.enteraname74.cloudy.domain.service.MusicService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*

fun Route.deleteSongs() {
    val musicService by inject<MusicService>()

    delete {
        val musicIds: List<String> = call.receive()
        val uuids: List<UUID> = musicIds.mapNotNull { UUIDUtils.fromString(it) }

        val username: String = getUsernameFromToken() ?: return@delete missingTokenInformation()
        val userId: UUID = getUserIdFromToken() ?: return@delete missingTokenInformation()

        val routingMessages: RoutingMessages = getRoutingMessages()

        uuids.forEach { musicId ->
            val isPossessedByUser = musicService.isMusicPossessedByUser(
                musicId = musicId,
                userId = userId,
            )

            if (!isPossessedByUser) {
                return@delete forbidden(routingMessages.songNotPossessedByUser(musicId))
            }
        }

        musicService.deleteAll(
            musicIds = uuids,
            username = username,
        )

        response(
            status = HttpStatusCode.OK,
            message = routingMessages.SONGS_DELETED,
        )
    }
}