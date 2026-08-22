package com.github.enteraname74.cloudy.domain.migration

// TODO DATABASE remove driver, user and password to simplify config?
interface DatabaseManager {

    /**
     * Handles database connection and migration.
     */
    suspend fun manage(
        url: String,
        driver: String,
        user: String,
        password: String,
    )
}