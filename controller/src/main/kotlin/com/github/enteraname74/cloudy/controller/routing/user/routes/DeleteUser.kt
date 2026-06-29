package com.github.enteraname74.cloudy.controller.routing.user.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.*
import com.github.enteraname74.cloudy.controller.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.service.UserService
import io.ktor.http.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.deleteUser() {
    val userService: UserService by inject()

    delete("/{userId}") {
        val tokenUserId: Uuid = getUserIdFromToken() ?: return@delete missingTokenInformation()

        val routingMessages: RoutingMessages = getRoutingMessages()

        val userIdToDelete: Uuid = Uuid.parseOrNull(call.parameters["userId"].orEmpty()) ?: return@delete badRequest(
            message = routingMessages.WRONG_ID
        )

        if (!userService.canDeleteUser(requester = tokenUserId, userIdToDelete = userIdToDelete)) {
            return@delete forbidden(routingMessages.MISSING_PERMISSION_FOR_DELETION)
        }

        userService.deleteUser(userId = userIdToDelete)

        response(
            status = HttpStatusCode.OK,
            message = routingMessages.USER_DELETED,
        )
    }
}