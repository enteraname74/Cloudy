package com.github.enteraname74.cloudy.localdb.util

import com.github.enteraname74.cloudy.domain.ext.ensureExist
import com.github.enteraname74.cloudy.domain.migration.DatabaseSetup
import java.io.File

object AppFileUtils {
    fun ensureFolderExist(path: String): File =
        File(APP_FOLDER, path).ensureExist()

    fun ensureParentFolderExist(path: String): File =
        File(APP_FOLDER, path).apply { parentFile?.ensureExist() }

    fun get(path: String): File =
        File(APP_FOLDER, path)

    // TODO V2: Check should be done on if the project is dockerized or not.
    private fun isUsingSQLite(): Boolean =
        DatabaseSetup.fromEnvironment() is DatabaseSetup.Sqlite

    private val APP_FOLDER: File = File(
        if (isUsingSQLite()) {
            "cloudy_data"
        } else {
            "/cloudy_data"
        }
    ).ensureExist()
}