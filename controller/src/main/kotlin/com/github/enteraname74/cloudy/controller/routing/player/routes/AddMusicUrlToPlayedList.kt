package com.github.enteraname74.cloudy.controller.routing.player.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.config.auth.getUsernameFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.cannotFindUser
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.respond
import com.github.enteraname74.cloudy.controller.routing.player.model.AddMusicUrlToPlayedListBody
import com.github.enteraname74.cloudy.controller.routing.player.resource.PlayerResource
import com.github.enteraname74.cloudy.domain.model.music.Music
import com.github.enteraname74.cloudy.domain.model.user.User
import com.github.enteraname74.cloudy.domain.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.service.MusicService
import com.github.enteraname74.cloudy.domain.service.PlayerService
import com.github.enteraname74.cloudy.domain.service.UserService
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject

fun Route.addMusicUrlToPlayedList() {
    val playerService by inject<PlayerService>()
    val musicService by inject<MusicService>()
    val userService by inject<UserService>()

    post<PlayerResource.Url> {
        val routingMessages: RoutingMessages = getRoutingMessages()
        val body: AddMusicUrlToPlayedListBody = call.receive()
        val userId = getUserIdFromToken() ?: return@post missingTokenInformation()
        val username: String = getUsernameFromToken() ?: return@post missingTokenInformation()

        val user: User = userService.getUserFromUsername(username = username) ?: return@post cannotFindUser()

        val uploadedResult: CloudyResult<Music> = musicService.saveFromUrl(
            user = user,
            url = body.url,
        )

        when (uploadedResult) {
            is CloudyResult.Error -> {
                return@post badRequest(routingMessages.CANNOT_SAVE_SONG)
            }

            is CloudyResult.Success -> {
                val result = playerService.addMusics(
                    userId = userId,
                    deviceId = body.deviceId,
                    listId = body.listId,
                    musicIds = listOf(uploadedResult.data.fingerprint),
                    routingMessages = getRoutingMessages()
                )

                respond(result)
            }
        }
    }
}