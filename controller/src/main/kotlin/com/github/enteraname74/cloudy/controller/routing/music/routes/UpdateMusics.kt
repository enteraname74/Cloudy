package com.github.enteraname74.cloudy.controller.routing.music.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.routing.music.resource.MusicResource
import com.github.enteraname74.cloudy.controller.util.MultiPartDataUtils
import com.github.enteraname74.cloudy.domain.model.music.Music
import com.github.enteraname74.cloudy.domain.model.music.MusicUpdatePayload
import com.github.enteraname74.cloudy.domain.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.service.MusicService
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.logging.cloudyLogger
import io.ktor.server.request.receiveMultipart
import io.ktor.server.resources.put
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.updateMusics() {
    val musicService by inject<MusicService>()

    put<MusicResource> {
        val userId: Uuid = getUserIdFromToken() ?: return@put missingTokenInformation()
        val routingMessages: RoutingMessages = getRoutingMessages()

        val payload: CloudyResult<MusicUpdatePayload> = MultiPartDataUtils.processMusicUpdateRequest(
            request = call.receiveMultipart(),
        )

        when (payload) {
            is CloudyResult.Error -> {
                cloudyLogger.error("Error while retrieving update song payload: $payload")
                return@put badRequest(routingMessages.WRONG_BODY_DATA)
            }
            is CloudyResult.Success -> {
                val result: CloudyResult<Music> = musicService.update(
                    payload = payload.data,
                    userId = userId,
                )

                when (result) {
                    is CloudyResult.Error -> {
                        cloudyLogger.error("Error while update song: $result")
                        badRequest(routingMessages.cannotUpdateSong(result.message.orEmpty()))
                    }
                    is CloudyResult.Success -> call.respond(result.data)
                }
            }
        }
    }
}