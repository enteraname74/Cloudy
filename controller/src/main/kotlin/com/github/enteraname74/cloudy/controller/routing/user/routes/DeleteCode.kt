package com.github.enteraname74.cloudy.controller.routing.user.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.respond
import com.github.enteraname74.cloudy.controller.routing.user.resource.UserResource
import com.github.enteraname74.cloudy.domain.service.UserService
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.deleteCode() {
    val userService: UserService by inject()

    delete<UserResource.Code> { code ->
        val userId: Uuid = getUserIdFromToken() ?: return@delete missingTokenInformation()

        respond(
            userService.deleteCode(
                userId = userId,
                code = code.id,
                routingMessages = getRoutingMessages()
            )
        )
    }
}