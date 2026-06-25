package com.github.enteraname74.cloudy.controller.routing.user.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.*
import com.github.enteraname74.cloudy.controller.routing.user.model.GeneratedCode
import com.github.enteraname74.cloudy.controller.routing.user.resource.UserResource
import com.github.enteraname74.cloudy.domain.model.user.User
import com.github.enteraname74.cloudy.domain.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.service.UserService
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.generateInscriptionCode() {
    val userService: UserService by inject()

    get<UserResource.GenerateCode> {
        val userId: Uuid = getUserIdFromToken() ?: return@get missingTokenInformation()

        val routingMessages: RoutingMessages = getRoutingMessages()

        val user: User = userService.getUserFromId(userId) ?: return@get badRequest(
            message = routingMessages.CANNOT_FIND_USER,
        )

        if (!user.isAdmin) {
            forbidden(routingMessages.NOT_AN_ADMIN)
        }

        respond(
            userService.generateCode(
                userId = userId,
                routingMessages = routingMessages,
            ).mapSuccess { GeneratedCode(it.code) }
        )
    }
}