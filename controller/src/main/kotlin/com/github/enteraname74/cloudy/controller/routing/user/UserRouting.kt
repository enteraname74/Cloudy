package com.github.enteraname74.cloudy.controller.routing.user

import com.github.enteraname74.cloudy.config.plugin.authenticatedRoutes
import com.github.enteraname74.cloudy.controller.routing.user.routes.allUser
import com.github.enteraname74.cloudy.controller.routing.user.routes.deleteUser
import com.github.enteraname74.cloudy.controller.routing.user.routes.generateInscriptionCode
import io.ktor.server.routing.*

fun Routing.userRoutes() {
    allUser()
    authenticatedRoutes {
        generateInscriptionCode()
        deleteUser()
    }
}