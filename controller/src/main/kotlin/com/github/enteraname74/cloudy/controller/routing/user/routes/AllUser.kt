package com.github.enteraname74.cloudy.controller.routing.user.routes

import com.github.enteraname74.cloudy.controller.routing.user.model.toUsableUser
import com.github.enteraname74.cloudy.controller.routing.user.resource.UserResource
import com.github.enteraname74.cloudy.domain.service.UserService
import io.ktor.server.resources.get
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.allUser() {
    val userService by inject<UserService>()

    get<UserResource> {
        call.respond(
            userService.getAll().map { it.toUsableUser() }
        )
    }
}