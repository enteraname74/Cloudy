package com.github.enteraname74.cloudy.controller.routing.user.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.routing.user.model.toSimpleUser
import com.github.enteraname74.cloudy.controller.routing.user.resource.UserResource
import com.github.enteraname74.cloudy.domain.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.service.UserService
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.allUser() {
    val userService by inject<UserService>()

    get<UserResource> {
        val userId: Uuid = getUserIdFromToken() ?: return@get missingTokenInformation()

        val routingMessages: RoutingMessages = getRoutingMessages()

        userService.getUserFromId(userId) ?: return@get badRequest(
            message = routingMessages.CANNOT_FIND_USER,
        )

        call.respond(
            userService.getAll().map { it.toSimpleUser() }
        )
    }
}