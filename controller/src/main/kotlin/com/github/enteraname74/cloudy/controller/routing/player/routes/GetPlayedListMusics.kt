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

fun Route.getPlayedListMusics() {
    val playerService by inject<PlayerService>()

    get<PlayerResource.GetMusics> { playerResource ->
        val userId = getUserIdFromToken() ?: return@get missingTokenInformation()

        val result = playerService.getMusics(
            listId = playerResource.listId,
            userId = userId,
            deviceId = playerResource.deviceId,
            paginatedRequest = playerResource.toPaginatedRequest(),
            routingMessages = getRoutingMessages(),
        )

        respond(result)
    }
}