package com.github.enteraname74.cloudy.fileaccess

class MusicFileManager(
    private val coverFileManager: CoverFileManager,
): FileManager() {
    override fun getFileDirectory(username: String): String =
        "${getUserDirectory(username = username)}/$MUSIC_FOLDER"
    
    override fun delete(name: String, username: String) {
        super.delete(name, username)
        // We delete its cover if any
        coverFileManager.delete(
            name = name,
            username = username,
        )
    }
    
    companion object {
        const val MUSIC_FOLDER = "musics"
    }
}