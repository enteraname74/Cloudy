package com.github.enteraname74.cloudy.config.plugin

import com.github.enteraname74.cloudy.domain.migration.BackendMigrationManager
import io.ktor.server.application.Application
import org.koin.ktor.ext.inject

internal suspend fun Application.manageBackendMigration() {
    val backendMigrationManager by inject<BackendMigrationManager>()

    backendMigrationManager.handleMigrations()
}