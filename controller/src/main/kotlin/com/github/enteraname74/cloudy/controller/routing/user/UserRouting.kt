package com.github.enteraname74.cloudy.controller.routing.user

import com.github.enteraname74.cloudy.config.plugin.authenticatedRoutes
import com.github.enteraname74.cloudy.controller.routing.user.routes.*
import io.ktor.server.routing.*

fun Routing.userRoutes() {
    authenticatedRoutes {
        allUser()
        generateInscriptionCode()
        deleteUser()
        allCodesOfUser()
        deleteCode()
        getUserStorage()
        deleteUserData()
    }
}