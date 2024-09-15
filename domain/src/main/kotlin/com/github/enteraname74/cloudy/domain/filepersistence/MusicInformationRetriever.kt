package com.github.enteraname74.cloudy.domain.filepersistence

import java.io.File
import java.util.UUID

interface MusicInformationRetriever {
    suspend fun getInformationAboutMusicFile(
        musicFile: File,
        musicId: UUID,
        shouldSearchForMetadata: Boolean,
    ): MusicInformationResult
}

sealed interface MusicInformationResult {
    data object Error: MusicInformationResult
    data class FileMetadata(
        val musicId: UUID,
        val name: String,
        val artist: String,
        val album: String,
        val coverPath: String?,
        val fingerprint: String,
        val duration: Long,
    ): MusicInformationResult
}