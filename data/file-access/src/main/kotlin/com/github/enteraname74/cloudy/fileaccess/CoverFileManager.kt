package com.github.enteraname74.cloudy.fileaccess

import com.github.enteraname74.cloudy.fileaccess.MusicFileManager.Companion.MUSIC_FOLDER

class CoverFileManager: FileManager() {

    override fun getFileDirectory(username: String): String =
        "${getUserDirectory(username)}/$MUSIC_FOLDER/$COVERS_FOLDER"

    companion object {
        private const val COVERS_FOLDER = "covers"
    }
}