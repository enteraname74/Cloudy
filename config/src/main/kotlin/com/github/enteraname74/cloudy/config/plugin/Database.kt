package com.github.enteraname74.cloudy.config.plugin

import com.github.enteraname74.cloudy.domain.migration.DatabaseManager
import com.github.enteraname74.cloudy.domain.migration.DatabaseSetup
import io.ktor.server.application.Application
import org.koin.ktor.ext.inject

suspend fun Application.configureDatabase() {
    val databaseManager by inject<DatabaseManager>()
    databaseManager.manage(
        setup = DatabaseSetup.fromEnvironment(),
    )
}