package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.model.Settings
import com.github.enteraname74.cloudy.domain.util.CloudyJson
import com.github.enteraname74.cloudy.localdb.util.AppFileUtils
import com.github.enteraname74.cloudy.repository.datasource.SettingsDataSource
import java.io.File

class SettingsDataSourceImpl : SettingsDataSource {
    private val SETTINGS_FILE: File = AppFileUtils.ensureParentFolderExist("settings.json")

    override suspend fun getSettings(): Settings? =
        runCatching {
            CloudyJson.decodeFromString<Settings>(SETTINGS_FILE.readText())
        }.getOrNull()

    override suspend fun saveSettings(settings: Settings) {
        SETTINGS_FILE.writeText(CloudyJson.encodeToString(settings))
    }
}