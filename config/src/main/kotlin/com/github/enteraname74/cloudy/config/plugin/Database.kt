package com.github.enteraname74.cloudy.config.plugin

import com.github.enteraname74.cloudy.localdb.AppDatabase
import io.ktor.server.application.*
import org.slf4j.LoggerFactory

fun Application.configureDatabase() {
    val logger = LoggerFactory.getLogger("Database")
    logger.info(
        "Will connect to ${environment.config.property("storage.user").getString()}"
    )
    AppDatabase.connectToDatabase(
        url = environment.config.property("storage.url").getString(),
        driver = environment.config.property("storage.driver").getString(),
        user = environment.config.property("storage.user").getString(),
        password = environment.config.property("storage.password").getString(),
    )
}