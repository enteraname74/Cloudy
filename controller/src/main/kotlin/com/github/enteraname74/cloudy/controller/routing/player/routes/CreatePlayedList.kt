package com.github.enteraname74.cloudy.controller.routing.player.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.respond
import com.github.enteraname74.cloudy.controller.routing.player.model.NewPlayedListBody
import com.github.enteraname74.cloudy.controller.routing.player.resource.PlayerResource
import com.github.enteraname74.cloudy.domain.service.PlayerService
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject

fun Route.createPlayedList() {
    val playerService by inject<PlayerService>()

    post<PlayerResource> {
        val newPlayedListBody: NewPlayedListBody = call.receive()
        val userId = getUserIdFromToken() ?: return@post missingTokenInformation()

        val result = playerService.create(
            hostId = userId,
            deviceId = newPlayedListBody.deviceId,
            initialMusicIds = newPlayedListBody.musicIds,
            routingMessages = getRoutingMessages(),
        )

        respond(result)
    }
}