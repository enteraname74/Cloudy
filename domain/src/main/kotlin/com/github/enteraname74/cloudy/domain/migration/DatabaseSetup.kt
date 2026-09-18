package com.github.enteraname74.cloudy.domain.migration

import com.github.enteraname74.cloudy.logging.CloudyLogger

sealed interface DatabaseSetup {
    val dbName: String
    val user: String
    val password: String
    val driver: String
    val url: String

    data class Sqlite(
        override val dbName: String,
        override val user: String,
        override val password: String,
    ) : DatabaseSetup {
        override val driver: String = "org.sqlite.JDBC"
        override val url: String = "jdbc:sqlite:$dbName.db?foreign_keys=on"
    }

    data class Postgres(
        override val dbName: String,
        override val user: String,
        override val password: String,
    ) : DatabaseSetup {
        override val driver: String = "org.postgresql.Driver"
        override val url: String = "jdbc:postgresql://postgres:5432/$dbName"
    }

    companion object {
        private val logger = CloudyLogger(this::class)

        fun fromEnvironment(): DatabaseSetup = try {
            val name = System.getenv("DB_NAME")
            val user = System.getenv("DB_USER")
            val password = System.getenv("DB_PASSWORD")

            when (val flavor = System.getenv("DB_FLAVOR")) {
                "postgres" -> Postgres(
                    dbName = name,
                    user = user,
                    password = password,
                )
                "sqlite" -> Sqlite(
                    dbName = name,
                    user = user,
                    password = password,
                )
                else -> throw Exception("Unknown flavor: $flavor")
            }

        } catch (e: Exception) {
            logger.error(
                message = "Error while building database setup from environment variables: ${e.message}",
            )
            throw e
        }
    }
}