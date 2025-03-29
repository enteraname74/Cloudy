package com.github.enteraname74.cloudy.controller.routing.auth.routes


import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.routing.auth.model.UserAuth
import com.github.enteraname74.cloudy.controller.routing.auth.model.UserLogin
import com.github.enteraname74.cloudy.controller.routing.auth.model.buildUserTokens
import com.github.enteraname74.cloudy.controller.routing.auth.model.toConnectedUser
import com.github.enteraname74.cloudy.controller.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.service.UserService
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.logIn() {
    val userService: UserService by inject()

    post("/login") {
        val user: UserLogin = call.receive()

        val routingMessages: RoutingMessages = getRoutingMessages()

        if (!user.isValid()) {
            return@post badRequest(message = routingMessages.MISSING_USER_INFORMATION)
        }

        val cloudyResult: CloudyResult<User> = userService.logUser(
            username = user.username,
            password = user.password,
        )

        when (cloudyResult) {
            is CloudyResult.Error -> {
                badRequest(message = routingMessages.WRONG_INFORMATION)
            }

            is CloudyResult.Success -> {
                val authenticatedUser = cloudyResult.data
                val tokens = buildUserTokens(user = authenticatedUser)
                call.respond(
                    UserAuth(
                        user = authenticatedUser.toConnectedUser(),
                        tokens = tokens,
                    )
                )
            }
        }
    }
}