package com.github.enteraname74.cloudy.controller.routing.player.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.respond
import com.github.enteraname74.cloudy.controller.routing.player.model.UpdateCurrentMusicBody
import com.github.enteraname74.cloudy.controller.routing.player.resource.PlayerResource
import com.github.enteraname74.cloudy.domain.service.PlayerService
import io.ktor.server.request.*
import io.ktor.server.resources.put
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject

fun Route.updateCurrentMusic() {
    val playerService by inject<PlayerService>()

    put<PlayerResource.Musics> {
        val body: UpdateCurrentMusicBody = call.receive()
        val userId = getUserIdFromToken() ?: return@put missingTokenInformation()

        val result = playerService.updateCurrentMusic(
            listId = body.listId,
            userId = userId,
            deviceId = body.deviceId,
            routingMessages = getRoutingMessages(),
            musicId = body.musicId
        )

        respond(result)
    }
}