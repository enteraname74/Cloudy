package com.github.enteraname74.cloudy.localdb

import com.github.enteraname74.cloudy.domain.migration.DatabaseManager
import com.github.enteraname74.cloudy.domain.model.Settings
import com.github.enteraname74.cloudy.domain.repository.SettingsRepository
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.localdb.table.AlbumTable
import com.github.enteraname74.cloudy.localdb.table.ArtistTable
import com.github.enteraname74.cloudy.localdb.table.MusicArtistTable
import com.github.enteraname74.cloudy.localdb.table.MusicPlaylistTable
import com.github.enteraname74.cloudy.localdb.table.MusicTable
import com.github.enteraname74.cloudy.localdb.table.PlaylistTable
import com.github.enteraname74.cloudy.localdb.table.UserInscriptionCodeTable
import com.github.enteraname74.cloudy.localdb.table.UserTable
import com.github.enteraname74.cloudy.localdb.table.player.PlayedListMusicTable
import com.github.enteraname74.cloudy.localdb.table.player.PlayedListTable
import com.github.enteraname74.cloudy.localdb.table.player.PlayedListUserTable
import com.github.enteraname74.cloudy.logging.CloudyLogger
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils

class DatabaseManagerImpl(
    private val settingsRepository: SettingsRepository,
) : DatabaseManager {
    private val logger = CloudyLogger(this::class)

    private fun initTables() {
        transaction {
            logger.info("Will create missing tables and columns")
            val migrationStatements = MigrationUtils.statementsRequiredForDatabaseMigration(
                *TABLES.toTypedArray()
            )
            if (migrationStatements.isNotEmpty()) {
                logger.info("Database migration required")
                migrationStatements.forEach {
                    exec(it)
                }
                logger.info("Database migration done")
            }
        }
    }

    private suspend fun handleMigration() {
        val settings = settingsRepository.getSettings()

        when {
            settings == null -> {
                logger.info("database is up to date")
                // Null correspond to a first install, so no migration is required, we will bump to the latest version.
                settingsRepository.saveSettings(
                    settings = Settings.CURRENT
                )
            }
            settings.dbVersion == Settings.CURRENT.dbVersion -> {
                logger.info("database is up to date")
                // up to date
            }
            else -> {
                /*
                Executes migration for each new version if needed.
                If the saved db version is 1, and the current db version is 4,
                it should execute migration 2, 3 and 4 if needed.
                 */
                for (version in (settings.dbVersion + 1)..Settings.CURRENT.dbVersion) {
                    logger.info("Executing migration for updating db to version $version")
                    val migration = MIGRATIONS.find { it.forVersion == version }
                    val result = migration?.execute()
                    when (result) {
                        is CloudyResult.Error -> {
                            logger.error("Failed to execute database migration for version $version, error: ${result.message}")
                            return
                        }
                        is CloudyResult.Success, null -> {
                            settingsRepository.saveSettings(
                                settings = settings.copy(
                                    dbVersion = version,
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    override suspend fun manage(
        url: String,
        driver: String,
        user: String,
        password: String,
    ) {
        Database.connect(
            url = url,
            driver = driver,
            user = user,
            password = password,
        )
        initTables()
        handleMigration()
    }

    private companion object {
        val TABLES = listOf(
            MusicTable,
            ArtistTable,
            AlbumTable,
            UserTable,
            PlaylistTable,
            MusicArtistTable,
            MusicPlaylistTable,
            PlayedListTable,
            PlayedListUserTable,
            PlayedListMusicTable,
            UserInscriptionCodeTable,
        )

        val MIGRATIONS: List<DatabaseMigration> = listOf()
    }
}