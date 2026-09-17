package com.github.enteraname74.cloudy.localdb.util

import com.github.enteraname74.cloudy.domain.ext.ensureExist
import java.io.File

object AppFileUtils {
    fun ensureFolderExist(path: String): File =
        File(APP_FOLDER, path).ensureExist()

    fun ensureParentFolderExist(path: String): File =
        File(APP_FOLDER, path).apply { parentFile?.ensureExist() }

    fun get(path: String): File =
        File(APP_FOLDER, path)

    private fun isUsingSQLite(): Boolean =
        System.getenv("DB_URL")?.contains("sqlite") ?: false

    private val APP_FOLDER: File = File(
        if (isUsingSQLite()) {
            "cloudy_data"
        } else {
            "/cloudy_data"
        }
    ).ensureExist()
}