package com.github.enteraname74.cloudy.fileaccess

import com.github.enteraname74.cloudy.domain.model.Settings
import com.github.enteraname74.cloudy.domain.util.CloudyJson
import java.io.File

class SettingsFileManager : FileManager() {
    override fun getFileDirectory(username: String): String = APP_FOLDER

    private val SETTING_FILE: File = File("$APP_FOLDER/$SETTINGS_FILE").apply {
        parentFile?.mkdirs()
    }

    fun saveSettings(settings: Settings) {
        SETTING_FILE.writeText(CloudyJson.encodeToString(settings))
    }

    fun getSettings(): Settings? =
        runCatching {
            CloudyJson.decodeFromString<Settings>(SETTING_FILE.readText())
        }.getOrNull()

    private companion object {
        const val SETTINGS_FILE = "settings.json"
    }
}