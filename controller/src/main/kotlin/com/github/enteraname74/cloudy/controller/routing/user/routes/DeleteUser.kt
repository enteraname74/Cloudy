package com.github.enteraname74.cloudy.controller.routing.user.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.forbidden
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.routing.user.resource.UserResource
import com.github.enteraname74.cloudy.domain.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.service.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.resources.delete
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.deleteUser() {
    val userService: UserService by inject()

    delete<UserResource.Delete> { userResource ->
        val tokenUserId: Uuid = getUserIdFromToken() ?: return@delete missingTokenInformation()

        val routingMessages: RoutingMessages = getRoutingMessages()

        if (!userService.canDeleteUser(
                requester = tokenUserId,
                userIdToDelete = userResource.id
            )
        ) {
            return@delete forbidden(routingMessages.MISSING_PERMISSION_FOR_DELETION)
        }

        userService.deleteUser(userId = userResource.id)

        response(
            status = HttpStatusCode.OK,
            message = routingMessages.USER_DELETED,
        )
    }
}