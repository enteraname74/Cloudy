package com.github.enteraname74.cloudy.controller.routing.music.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.config.auth.getUsernameFromToken
import com.github.enteraname74.cloudy.controller.ext.forbidden
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.routing.music.resource.MusicResource
import com.github.enteraname74.cloudy.domain.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.service.MusicService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.deleteSongs() {
    val musicService by inject<MusicService>()

    delete<MusicResource> {
        val musicIds: List<String> = call.receive()

        val username: String = getUsernameFromToken() ?: return@delete missingTokenInformation()
        val userId: Uuid = getUserIdFromToken() ?: return@delete missingTokenInformation()

        val routingMessages: RoutingMessages = getRoutingMessages()

        musicIds.forEach { musicId ->
            val doesExist = musicService.getFromId(musicId) != null
            if (doesExist) {
                val isPossessedByUser = musicService.isMusicPossessedByUser(
                    musicId = musicId,
                    userId = userId,
                )

                if (!isPossessedByUser) {
                    return@delete forbidden(routingMessages.songNotPossessedByUser(musicId))
                }
            }
        }

        musicService.deleteAll(
            musicIds = musicIds,
            username = username,
        )

        response(
            status = HttpStatusCode.OK,
            message = routingMessages.SONGS_DELETED,
        )
    }
}