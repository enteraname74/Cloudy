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
            val name = requiredEnvironmentVariable("DB_NAME")

            when (val flavor = requiredEnvironmentVariable("DB_FLAVOR")) {
                "postgres" -> Postgres(
                    dbName = name,
                    user = requiredEnvironmentVariable("DB_USER"),
                    password = requiredEnvironmentVariable("DB_PASSWORD"),
                )
                "sqlite" -> Sqlite(
                    dbName = name,
                    user = System.getenv("DB_USER").orEmpty(),
                    password = System.getenv("DB_PASSWORD").orEmpty(),
                )
                else -> throw Exception("Unknown flavor: $flavor")
            }

        } catch (e: Exception) {
            logger.error(
                message = "Error while building database setup from environment variables: ${e.message}",
            )
            throw e
        }

        private fun requiredEnvironmentVariable(name: String): String =
            System.getenv(name)
                ?.takeIf(String::isNotBlank)
                ?: error("Missing or empty environment variable: $name")
    }
}
