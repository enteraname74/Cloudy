package com.github.enteraname74.cloudy.config

import com.github.enteraname74.cloudy.config.plugin.*
import com.github.enteraname74.cloudy.domain.service.UserService
import io.ktor.server.application.*
import io.ktor.server.routing.*
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
//    configureSockets()
    configureStatusPage()
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