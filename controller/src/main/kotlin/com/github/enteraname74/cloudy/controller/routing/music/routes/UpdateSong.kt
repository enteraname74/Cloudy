package com.github.enteraname74.cloudy.controller.routing.music.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.routing.music.resource.MusicResource
import com.github.enteraname74.cloudy.controller.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.model.music.MusicUpdate
import com.github.enteraname74.cloudy.domain.service.MusicService
import com.github.enteraname74.cloudy.domain.service.UserService
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.updateSong() {
    val musicService by inject<MusicService>()
    val userService by inject<UserService>()

    put<MusicResource> {
        val userId: Uuid = getUserIdFromToken() ?: return@put missingTokenInformation()
        val routingMessages: RoutingMessages = getRoutingMessages()
        val user: User = userService.getUserFromId(userId) ?: return@put badRequest(
            message = routingMessages.CANNOT_FIND_USER,
        )

        val musicUpdates: List<MusicUpdate> = call.receive()

        val result: CloudyResult<Unit> = musicService.update(
            musicUpdates = musicUpdates,
            user = user,
        )

        if (result is CloudyResult.Error) {
            badRequest(routingMessages.cannotUpdateSong(result.message.orEmpty()))
        } else {
            call.respond(HttpStatusCode.OK)
        }
    }
}