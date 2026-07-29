package com.github.enteraname74.cloudy.controller.routing.player.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.respond
import com.github.enteraname74.cloudy.controller.routing.player.model.JoinPlayedListBody
import com.github.enteraname74.cloudy.controller.routing.player.resource.PlayerResource
import com.github.enteraname74.cloudy.domain.service.PlayerService
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject

fun Route.joinPlayedList() {
    val playerService by inject<PlayerService>()

    post<PlayerResource.Join> {
        val joinPayedListBody: JoinPlayedListBody = call.receive()
        val userId = getUserIdFromToken() ?: return@post missingTokenInformation()

        val result = playerService.join(
            userId = userId,
            code = joinPayedListBody.code,
            deviceId = joinPayedListBody.deviceId,
            routingMessages = getRoutingMessages(),
        )

        respond(result)
    }
}