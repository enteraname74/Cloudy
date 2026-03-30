package com.github.enteraname74.cloudy.config

import com.github.enteraname74.cloudy.config.plugin.configureAuthentication
import com.github.enteraname74.cloudy.config.plugin.configureDI
import com.github.enteraname74.cloudy.config.plugin.configureDatabase
import com.github.enteraname74.cloudy.config.plugin.configureHTTP
import com.github.enteraname74.cloudy.config.plugin.configureSerialization
import com.github.enteraname74.cloudy.config.plugin.configureSockets
import com.github.enteraname74.cloudy.domain.service.UserService
import io.ktor.server.application.Application
import io.ktor.server.routing.RoutingContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.ktor.ext.inject

fun Application.configureApplication() {
    configureDI()
    configureAuthentication()
    configureDatabase()
    configureSerialization()
    configureHTTP()
    configureSockets()
    upsertAdmin()
}

fun Application.upsertAdmin() {
    val userService by inject<UserService>()

    CoroutineScope(Dispatchers.IO).launch {
        val username: String? = System.getenv("ADMIN_USERNAME")
        val password: String? = System.getenv("ADMIN_PASSWORD")

        if (username == null || password == null) {
            return@launch
        }

        if (userService.getUserFromUsername(username = username) != null) {
            return@launch
        }

        userService.createUser(
            username = username,
            password = password,
            isAdmin = true,
        )
    }
}

typealias ApplicationContext = RoutingContext