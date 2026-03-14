package com.github.enteraname74.cloudy.controller.routing.auth.routes

import com.github.enteraname74.cloudy.config.auth.isTokenValid
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.safeReceive
import com.github.enteraname74.cloudy.controller.ext.wrongBody
import com.github.enteraname74.cloudy.controller.routing.auth.model.UserAuth
import com.github.enteraname74.cloudy.controller.routing.auth.model.UserSignIn
import com.github.enteraname74.cloudy.controller.routing.auth.model.buildUserTokens
import com.github.enteraname74.cloudy.controller.routing.auth.model.toConnectedUser
import com.github.enteraname74.cloudy.controller.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.service.UserService
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.signIn() {
    val userService by inject<UserService>()

    post("/sign") {
        val user: UserSignIn = call.receive()

        val routingMessages: RoutingMessages = getRoutingMessages()

        if (userService.isUsernameUsed(user.username)) {
            return@post badRequest(message = routingMessages.USERNAME_TAKEN)
        }

        if (!isTokenValid(token = user.inscriptionCode)) {
            return@post badRequest(message = routingMessages.INVALID_INSCRIPTION_CODE)
        }

        val cloudyResult: CloudyResult<User> = userService.createUser(
            username = user.username,
            password = user.password,
        )

        when (cloudyResult) {
            is CloudyResult.Error -> {
                badRequest(message = routingMessages.CANNOT_CREATE_USER)
            }

            is CloudyResult.Success -> {
                val savedUser: User = cloudyResult.data
                val tokens = buildUserTokens(user = savedUser)
                call.respond(
                    UserAuth(
                        user = savedUser.toConnectedUser(),
                        tokens = tokens,
                    )
                )
            }
        }
    }
}