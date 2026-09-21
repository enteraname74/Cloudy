package com.github.enteraname74.cloudy.controller.routing.auth

import com.github.enteraname74.cloudy.config.plugin.refreshAuthenticatedRoutes
import com.github.enteraname74.cloudy.controller.routing.auth.routes.logIn
import com.github.enteraname74.cloudy.controller.routing.auth.routes.refreshTokens
import com.github.enteraname74.cloudy.controller.routing.auth.routes.signIn
import io.ktor.server.routing.Routing

fun Routing.authRouting() {
    signIn()
    logIn()
    refreshAuthenticatedRoutes {
        refreshTokens()
    }
}
