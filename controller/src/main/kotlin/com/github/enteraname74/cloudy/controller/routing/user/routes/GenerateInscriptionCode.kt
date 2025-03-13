package com.github.enteraname74.cloudy.controller.routing.user.routes

import com.github.enteraname74.cloudy.config.auth.generateInscriptionToken
import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.forbidden
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.routing.user.model.GeneratedCode
import com.github.enteraname74.cloudy.controller.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.service.UserService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*

fun Route.generateInscriptionCode() {
    val userService: UserService by inject()

    get("/generateCode") {
        val userId: UUID = getUserIdFromToken() ?: return@get missingTokenInformation()

        val routingMessages: RoutingMessages = getRoutingMessages()

        val user: User = userService.getUserFromId(userId) ?: return@get badRequest(
            message = routingMessages.CANNOT_FIND_USER,
        )

        if (!user.isAdmin) {
            forbidden(routingMessages.NOT_AN_ADMIN)
        }

        call.respond(
            GeneratedCode(
                code = generateInscriptionToken()
            )
        )
    }
}