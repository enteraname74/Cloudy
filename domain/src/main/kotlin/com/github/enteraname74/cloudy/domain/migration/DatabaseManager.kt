package com.github.enteraname74.cloudy.domain.migration

interface DatabaseManager {

    /**
     * Handles database connection and migration.
     */
    suspend fun manage(setup: DatabaseSetup)
}