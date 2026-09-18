package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.Settings
import com.github.enteraname74.cloudy.domain.repository.SettingsRepository
import com.github.enteraname74.cloudy.repository.datasource.SettingsDataSource

class SettingsRepositoryImpl(
    private val dataSource: SettingsDataSource,
) : SettingsRepository {
    override suspend fun getSettings(): Settings? =
        dataSource.getSettings()

    override suspend fun saveSettings(settings: Settings) {
        dataSource.saveSettings(settings)
    }
}