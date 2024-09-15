package com.github.enteraname74.cloudy.config

import com.github.enteraname74.cloudy.config.plugin.*
import com.github.enteraname74.cloudy.config.plugin.configureHTTP
import com.github.enteraname74.cloudy.config.plugin.configureSerialization
import com.github.enteraname74.cloudy.config.plugin.configureSockets
import io.ktor.server.application.*

fun Application.configureApplication() {
    configureDI()
    configureAuthentication()
    configureDatabase()
    configureSerialization()
    configureHTTP()
    configureSockets()
    configureSecurity()
}