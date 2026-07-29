package com.github.enteraname74.cloudy.controller.routing.player.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.respond
import com.github.enteraname74.cloudy.controller.routing.player.model.CheckPlayerMusicIdsBody
import com.github.enteraname74.cloudy.controller.routing.player.resource.PlayerResource
import com.github.enteraname74.cloudy.domain.service.PlayerService
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject

fun Route.checkPlayerMusicIds() {
    val playerService: PlayerService by inject()

    post<PlayerResource.Check> {
        val userId = getUserIdFromToken() ?: return@post missingTokenInformation()

        val body: CheckPlayerMusicIdsBody = call.receive()
        val result: CloudyResult<List<String>> = playerService.getDeletedMusicIds(
            listId = body.listId,
            userId = userId,
            deviceId = body.deviceId,
            routingMessages = getRoutingMessages(),
            musicIds = body.musicIds,
        )

        respond(result)
    }
}