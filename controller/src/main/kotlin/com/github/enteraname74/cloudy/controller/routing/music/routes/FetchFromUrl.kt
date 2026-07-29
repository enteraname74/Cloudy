package com.github.enteraname74.cloudy.controller.routing.music.routes

import com.github.enteraname74.cloudy.config.auth.getUsernameFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.cannotFindUser
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.routing.music.model.FetchFromUrlBody
import com.github.enteraname74.cloudy.controller.routing.music.resource.MusicResource
import com.github.enteraname74.cloudy.domain.model.music.Music
import com.github.enteraname74.cloudy.domain.model.user.User
import com.github.enteraname74.cloudy.domain.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.service.MusicService
import com.github.enteraname74.cloudy.domain.service.UserService
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject

// TODO YT: Add full directory check
fun Route.fetchFromUrl() {
    val musicService by inject<MusicService>()
    val userService by inject<UserService>()

    post<MusicResource.FetchFromUrl> {
        val data: FetchFromUrlBody = call.receive()
        val routingMessages: RoutingMessages = getRoutingMessages()

        val username: String = getUsernameFromToken() ?: return@post missingTokenInformation()

        val user: User = userService.getUserFromUsername(
            username = username
        ) ?: return@post cannotFindUser()

        val uploadedResult: CloudyResult<Music> = musicService.saveFromUrl(
            user = user,
            url = data.url,
        )

        when (uploadedResult) {
            is CloudyResult.Error -> {
                return@post badRequest(routingMessages.CANNOT_SAVE_SONG)
            }

            is CloudyResult.Success -> {
                call.respond(uploadedResult.data)
            }
        }
    }
}