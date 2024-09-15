package com.github.enteraname74.cloudy.controller.routing.auth


import com.github.enteraname74.cloudy.controller.routing.auth.routes.logIn
import com.github.enteraname74.cloudy.controller.routing.auth.routes.signIn
import io.ktor.server.routing.*


fun Routing.authRouting() {
    route("/auth") {
        signIn()
        logIn()
    }
}