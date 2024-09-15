package com.github.enteraname74.cloudy.config.plugin

import com.github.enteraname74.cloudy.localdb.AppDatabase
import io.ktor.server.application.*

fun Application.configureDatabase() {
    AppDatabase.connectToDatabase(
        url = environment.config.property("storage.url").getString(),
        driver = environment.config.property("storage.driver").getString(),
    )
}