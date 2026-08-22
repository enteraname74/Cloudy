package com.github.enteraname74.cloudy.config

import com.github.enteraname74.cloudy.config.plugin.configureAuthentication
import com.github.enteraname74.cloudy.config.plugin.configureDI
import com.github.enteraname74.cloudy.config.plugin.configureDatabase
import com.github.enteraname74.cloudy.config.plugin.configureHTTP
import com.github.enteraname74.cloudy.config.plugin.configureSerialization
import com.github.enteraname74.cloudy.config.plugin.configureSockets
import com.github.enteraname74.cloudy.config.plugin.manageBackendMigration
import com.github.enteraname74.cloudy.domain.model.user.UserType
import com.github.enteraname74.cloudy.domain.service.UserService
import io.ktor.server.application.Application
import io.ktor.server.routing.RoutingContext
import org.koin.ktor.ext.inject

suspend fun Application.configureApplication() {
    configureDI()
    manageBackendMigration()
    configureDatabase()
    configureAuthentication()
    configureSerialization()
    configureHTTP()
    configureSockets()
    upsertAdmin()
}

suspend fun Application.upsertAdmin() {
    val userService by inject<UserService>()

    val username: String? = System.getenv("ADMIN_USERNAME")
    val password: String? = System.getenv("ADMIN_PASSWORD")

    if (username == null || password == null) {
        return
    }

    if (userService.getUserFromUsername(username = username) != null) {
        return
    }

    userService.createUser(
        username = username,
        password = password,
        type = UserType.Admin,
    )
}

typealias ApplicationContext = RoutingContext