package com.github.enteraname74.cloudy.domain.filepersistence

import java.util.UUID

object FilePersistenceUtils {
    const val APP_FOLDER = "app"
    const val MUSIC_FOLDER = "musics"

    fun getUUIDFromFileName(fileName: String): UUID? =
        runCatching {
            UUID.fromString(
                fileName.replaceFirst(
                    regex = """[.][^.]+$""".toRegex(),
                    replacement = ""
                )
            )
        }.getOrNull()
}