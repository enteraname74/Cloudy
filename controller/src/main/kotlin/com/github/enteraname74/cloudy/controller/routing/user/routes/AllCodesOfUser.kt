package com.github.enteraname74.cloudy.controller.routing.user.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.routing.user.resource.UserResource
import com.github.enteraname74.cloudy.domain.service.UserService
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.allCodesOfUser() {
    val userService: UserService by inject()

    get<UserResource.AllCodes> {
        val userId: Uuid = getUserIdFromToken() ?: return@get missingTokenInformation()

        call.respond(userService.getAllCodesOfUser(userId))
    }
}