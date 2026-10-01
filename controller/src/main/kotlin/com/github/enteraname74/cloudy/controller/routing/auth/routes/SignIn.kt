package com.github.enteraname74.cloudy.controller.routing.auth.routes

import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.routing.auth.model.UserAuth
import com.github.enteraname74.cloudy.controller.routing.auth.model.UserSignIn
import com.github.enteraname74.cloudy.controller.routing.auth.model.buildUserTokens
import com.github.enteraname74.cloudy.controller.routing.auth.resource.AuthResource
import com.github.enteraname74.cloudy.controller.routing.user.model.toSimpleUser
import com.github.enteraname74.cloudy.domain.model.user.User
import com.github.enteraname74.cloudy.domain.model.user.UserType
import com.github.enteraname74.cloudy.domain.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.service.UserService
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject

fun Route.signIn() {
    val userService by inject<UserService>()

    post<AuthResource.SignIn> {
        val user: UserSignIn = call.receive()

        val routingMessages: RoutingMessages = getRoutingMessages()

        val cloudyResult: CloudyResult<User> = userService.createUserWithInscriptionCode(
            username = user.username,
            password = user.password,
            type = UserType.User,
            inscriptionCode = user.inscriptionCode,
            routingMessages = routingMessages,
        )

        when (cloudyResult) {
            is CloudyResult.Error -> {
                badRequest(message = cloudyResult.message ?: routingMessages.CANNOT_CREATE_USER)
            }

            is CloudyResult.Success -> {
                val savedUser: User = cloudyResult.data
                val tokens = buildUserTokens(user = savedUser)
                call.respond(
                    UserAuth(
                        user = savedUser.toSimpleUser(),
                        tokens = tokens,
                    )
                )
            }
        }
    }
}
