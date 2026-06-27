package com.github.enteraname74.cloudy.controller.routing.auth.routes


import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.routing.auth.model.UserAuth
import com.github.enteraname74.cloudy.controller.routing.auth.model.UserLogin
import com.github.enteraname74.cloudy.controller.routing.auth.model.buildUserTokens
import com.github.enteraname74.cloudy.controller.routing.auth.resource.AuthResource
import com.github.enteraname74.cloudy.controller.routing.user.model.toSimpleUser
import com.github.enteraname74.cloudy.domain.model.user.User
import com.github.enteraname74.cloudy.domain.service.UserService
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject

fun Route.logIn() {
    val userService: UserService by inject()

    post<AuthResource.LogIn> {
        val user: UserLogin = call.receive()

        val cloudyResult: CloudyResult<User> = userService.logUser(
            username = user.username,
            password = user.password,
        )

        when (cloudyResult) {
            is CloudyResult.Error -> {
                badRequest(message = getRoutingMessages().WRONG_INFORMATION)
            }

            is CloudyResult.Success -> {
                val authenticatedUser = cloudyResult.data
                val tokens = buildUserTokens(user = authenticatedUser)
                call.respond(
                    UserAuth(
                        user = authenticatedUser.toSimpleUser(),
                        tokens = tokens,
                    )
                )
            }
        }
    }
}