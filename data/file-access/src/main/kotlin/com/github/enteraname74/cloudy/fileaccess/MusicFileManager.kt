package com.github.enteraname74.cloudy.fileaccess

import java.util.*

class MusicFileManager(
    private val coverFileManager: CoverFileManager,
): FileManager() {
    override fun getFileDirectory(username: String): String =
        "${getUserDirectory(username = username)}/$MUSIC_FOLDER"
    
    override fun delete(id: UUID, username: String) {
        super.delete(id, username)
        // We delete its cover if any
        coverFileManager.delete(
            id = id,
            username = username,
        )
    }
    
    companion object {
        const val MUSIC_FOLDER = "musics"
    }
}