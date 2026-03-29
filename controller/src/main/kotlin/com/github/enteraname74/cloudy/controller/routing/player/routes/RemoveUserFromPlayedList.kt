package com.github.enteraname74.cloudy.controller.routing.player.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.respond
import com.github.enteraname74.cloudy.controller.routing.player.model.RemoveUserFromPlayedListBody
import com.github.enteraname74.cloudy.controller.routing.player.resource.PlayerResource
import com.github.enteraname74.cloudy.domain.service.PlayerService
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.removeUserFromPlayedList() {
    val playerService by inject<PlayerService>()

    delete<PlayerResource.RemoveUser> {
        val body: RemoveUserFromPlayedListBody = call.receive()
        val userId = getUserIdFromToken() ?: return@delete missingTokenInformation()

        val result = playerService.remove(
            userId = userId,
            listId = body.listId,
            deviceId = body.deviceId,
            userIdToRemove = body.userIdToRemove,
            deviceIdToRemove = body.deviceIdToRemove,
            routingMessages = getRoutingMessages()
        )

        respond(result)
    }
}