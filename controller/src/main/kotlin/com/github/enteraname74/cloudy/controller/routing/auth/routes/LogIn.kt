package com.github.enteraname74.cloudy.controller.routing.auth.routes


import com.github.enteraname74.cloudy.config.auth.generateToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.routing.auth.model.UserAuth
import com.github.enteraname74.cloudy.controller.util.RoutingMessages
import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.service.UserService
import com.github.enteraname74.cloudy.domain.util.ServiceResult
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.logIn() {
    val userService: UserService by inject()

    post("/login") {
        val user: UserAuth = call.receive()

        if (!user.isValid()) {
            return@post badRequest(message = RoutingMessages.User.MISSING_INFORMATION)
        }

        val serviceResult: ServiceResult = userService.logUser(
            username = user.username,
            password = user.password,
        )

        when (serviceResult) {
            is ServiceResult.Error -> {
                badRequest(message = RoutingMessages.User.WRONG_INFORMATION)
            }

            is ServiceResult.Ok -> {
                val authenticatedUser = (serviceResult.data as User)
                val token: String = generateToken(user = authenticatedUser)
                call.respond(
                    message = hashMapOf("token" to token)
                )
            }
        }
    }
}