package com.github.enteraname74.cloudy.controller.routing.user.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.config.auth.getUsernameFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.routing.user.resource.UserResource
import com.github.enteraname74.cloudy.domain.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.service.UserService
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.getUserStorage() {
    val userService by inject<UserService>()

    get<UserResource.Storage> {
        val userId: Uuid = getUserIdFromToken() ?: return@get missingTokenInformation()

        val routingMessages: RoutingMessages = getRoutingMessages()
        val username = getUsernameFromToken() ?: return@get badRequest(
            message = routingMessages.CANNOT_FIND_USER,
        )

        userService.getUserFromId(userId) ?: return@get badRequest(
            message = routingMessages.CANNOT_FIND_USER,
        )

        call.respond(
            userService.getUserStorage(username = username)
        )
    }
}