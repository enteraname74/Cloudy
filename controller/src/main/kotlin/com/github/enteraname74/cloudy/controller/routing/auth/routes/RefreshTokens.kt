package com.github.enteraname74.cloudy.controller.routing.auth.routes

import com.github.enteraname74.cloudy.config.auth.getUsernameFromToken
import com.github.enteraname74.cloudy.config.auth.isTokenARefreshOne
import com.github.enteraname74.cloudy.controller.ext.cannotFindUser
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.routing.auth.model.buildUserTokens
import com.github.enteraname74.cloudy.controller.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.service.UserService
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.refreshTokens() {
    val userService by inject<UserService>()

    get("/refreshTokens") {

        if (!isTokenARefreshOne()) {
            val routingMessages: RoutingMessages = getRoutingMessages()

            return@get response(
                status = HttpStatusCode.BadRequest,
                message = routingMessages.NOT_A_REFRESH_TOKEN,
            )
        }

        val username = getUsernameFromToken() ?: return@get missingTokenInformation()

        val user: User = userService.getUserFromUsername(username) ?: return@get cannotFindUser()

        call.respond(
            buildUserTokens(user)
        )
    }
}