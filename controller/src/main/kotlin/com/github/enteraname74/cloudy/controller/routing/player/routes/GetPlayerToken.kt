package com.github.enteraname74.cloudy.controller.routing.player.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.routing.player.model.buildPlayerToken
import com.github.enteraname74.cloudy.controller.routing.player.resource.PlayerResource
import com.github.enteraname74.cloudy.domain.model.user.User
import com.github.enteraname74.cloudy.domain.service.UserService
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.getPlayerToken() {
    val userService by inject<UserService>()

    get<PlayerResource.Token> {
        val userId = getUserIdFromToken() ?: return@get missingTokenInformation()

        val user: User = userService.getUserFromId(userId) ?: return@get badRequest(
            message = getRoutingMessages().CANNOT_FIND_USER,
        )

        call.respond(buildPlayerToken(user))
    }
}