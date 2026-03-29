package com.github.enteraname74.cloudy.controller.routing.player.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.respond
import com.github.enteraname74.cloudy.controller.routing.player.resource.PlayerResource
import com.github.enteraname74.cloudy.domain.service.PlayerService
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.deletePlayedList() {
    val playerService by inject<PlayerService>()

    delete<PlayerResource.List> { playerResource ->
        val userId = getUserIdFromToken() ?: return@delete missingTokenInformation()

        val result = playerService.delete(
            listId = playerResource.listId,
            userId = userId,
            deviceId = playerResource.deviceId,
            routingMessages = getRoutingMessages(),
        )

        respond(result)
    }
}