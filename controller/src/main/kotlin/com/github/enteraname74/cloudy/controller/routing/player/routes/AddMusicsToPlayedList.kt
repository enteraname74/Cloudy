package com.github.enteraname74.cloudy.controller.routing.player.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.respond
import com.github.enteraname74.cloudy.controller.routing.player.model.MusicsOperationOnPlayedListBody
import com.github.enteraname74.cloudy.controller.routing.player.resource.PlayerResource
import com.github.enteraname74.cloudy.domain.service.PlayerService
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject

fun Route.addMusicsToPlayedList() {
    val playerService by inject<PlayerService>()

    post<PlayerResource.Musics> {
        val body: MusicsOperationOnPlayedListBody = call.receive()
        val userId = getUserIdFromToken() ?: return@post missingTokenInformation()

        val result = playerService.addMusics(
            userId = userId,
            deviceId = body.deviceId,
            listId = body.listId,
            musicIds = body.musicIds,
            routingMessages = getRoutingMessages()
        )

        respond(result)
    }
}