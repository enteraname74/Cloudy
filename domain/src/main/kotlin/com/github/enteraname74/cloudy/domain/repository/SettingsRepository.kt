package com.github.enteraname74.cloudy.domain.repository

import com.github.enteraname74.cloudy.domain.model.Settings

interface SettingsRepository {
    suspend fun getSettings(): Settings?

    suspend fun saveSettings(settings: Settings)
}