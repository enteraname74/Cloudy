package com.github.enteraname74.cloudy.config.plugin

import com.github.enteraname74.cloudy.domain.migration.DatabaseManager
import io.ktor.server.application.Application
import org.koin.ktor.ext.inject

suspend fun Application.configureDatabase() {
    val databaseManager by inject<DatabaseManager>()
    databaseManager.manage(
        url = environment.config.property("storage.url").getString(),
        driver = environment.config.property("storage.driver").getString(),
        user = environment.config.propertyOrNull("storage.user")?.getString().orEmpty(),
        password = environment.config.propertyOrNull("storage.password")?.getString().orEmpty(),
    )
}