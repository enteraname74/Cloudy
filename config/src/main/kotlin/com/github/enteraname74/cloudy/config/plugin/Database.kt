package com.github.enteraname74.cloudy.config.plugin

import com.github.enteraname74.cloudy.localdb.AppDatabase
import io.ktor.server.application.Application

fun Application.configureDatabase() {
    AppDatabase.connectToDatabase(
        url = environment.config.property("storage.url").getString(),
        driver = environment.config.property("storage.driver").getString(),
        user = environment.config.propertyOrNull("storage.user")?.getString().orEmpty(),
        password = environment.config.propertyOrNull("storage.password")?.getString().orEmpty(),
    )
}