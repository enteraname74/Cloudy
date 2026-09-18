package com.github.enteraname74.cloudy.repository.datasource

import com.github.enteraname74.cloudy.domain.model.Settings

interface SettingsDataSource {
    suspend fun getSettings(): Settings?

    suspend fun saveSettings(settings: Settings)
}