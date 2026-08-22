package com.github.enteraname74.cloudy.domain.migration

import com.github.enteraname74.cloudy.domain.model.Settings
import com.github.enteraname74.cloudy.domain.repository.SettingsRepository
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.logging.CloudyLogger

class BackendMigrationManager(
    private val settingsRepository: SettingsRepository,
) {
    private val logger = CloudyLogger(this::class)

    suspend fun handleMigrations() {
        logger.info("Checking for backend migration")
        val settings = settingsRepository.getSettings()

        when {
            settings == null -> {
                logger.info("backend is up to date")
                // Null correspond to a first install, so no migration is required, we will bump to the latest version.
                settingsRepository.saveSettings(
                    settings = Settings.CURRENT
                )
            }
            settings.backendVersion == Settings.CURRENT.backendVersion -> {
                logger.info("backend is up to date")
            }
            else -> {
                /*
                Executes migration for each new version if needed.
                If the saved backend version is 1, and the current backend version is 4,
                it should execute migration 2, 3 and 4 if needed.
                 */
                for (version in (settings.backendVersion + 1)..Settings.CURRENT.backendVersion) {
                    logger.info("Executing migration for updating backend to version $version")
                    val migration = MIGRATIONS.find { it.forVersion == version }
                    val result = migration?.execute()
                    when (result) {
                        is CloudyResult.Error -> {
                            logger.error("Failed to execute backend migration for version $version, error: ${result.message}")
                            return
                        }
                        is CloudyResult.Success, null -> {
                            settingsRepository.saveSettings(
                                settings = settings.copy(
                                    backendVersion = version,
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private companion object {
        val MIGRATIONS: List<BackendMigration> = listOf()
    }
}