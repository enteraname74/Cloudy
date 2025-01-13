package com.github.enteraname74.cloudy.controller.routing.user

import com.github.enteraname74.cloudy.controller.routing.user.routes.allUser
import io.ktor.server.routing.*

fun Routing.userRoutes() {
    route("/users") {
        allUser()
    }
}