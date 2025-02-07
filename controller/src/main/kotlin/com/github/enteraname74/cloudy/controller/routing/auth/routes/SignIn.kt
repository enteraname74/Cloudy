package com.github.enteraname74.cloudy.controller.routing.auth.routes

import com.github.enteraname74.cloudy.config.auth.isTokenValid
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.safeReceive
import com.github.enteraname74.cloudy.controller.ext.wrongBody
import com.github.enteraname74.cloudy.controller.routing.auth.model.UserAuth
import com.github.enteraname74.cloudy.controller.routing.auth.model.UserSignIn
import com.github.enteraname74.cloudy.controller.routing.auth.model.buildUserTokens
import com.github.enteraname74.cloudy.controller.routing.auth.model.toConnectedUser
import com.github.enteraname74.cloudy.controller.util.RoutingMessages
import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.service.UserService
import com.github.enteraname74.cloudy.domain.util.ServiceResult
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.signIn() {
    val userService by inject<UserService>()

    post("/sign") {
        val user: UserSignIn = call.safeReceive() ?: return@post wrongBody()

        if (!user.isValid()) {
            return@post badRequest(message = RoutingMessages.User.MISSING_INFORMATION)
        }

        if (userService.isUsernameUsed(user.username)) {
            return@post badRequest(message = RoutingMessages.User.USERNAME_TAKEN)
        }

        if (!isTokenValid(token = user.inscriptionCode)) {
            return@post badRequest(message = RoutingMessages.User.INVALID_INSCRIPTION_CODE)
        }

        val serviceResult: ServiceResult = userService.createUser(
            username = user.username,
            password = user.password,
        )

        when (serviceResult) {
            is ServiceResult.Error -> {
                badRequest(message = RoutingMessages.User.CANNOT_CREATE_USER)
            }

            is ServiceResult.Ok -> {
                val savedUser: User = (serviceResult.data as User)
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