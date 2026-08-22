package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.Settings
import com.github.enteraname74.cloudy.domain.repository.SettingsRepository
import com.github.enteraname74.cloudy.fileaccess.SettingsFileManager

class SettingsRepositoryImpl(
    private val settingsFileManager: SettingsFileManager,
) : SettingsRepository {
    override suspend fun getSettings(): Settings? =
        settingsFileManager.getSettings()

    override suspend fun saveSettings(settings: Settings) {
        settingsFileManager.saveSettings(settings)
    }
}