package com.github.enteraname74.cloudy.domain.filepersistence

import com.github.enteraname74.cloudy.domain.model.CustomMusicMetadata
import java.io.File

interface MusicInformationRetriever {

    suspend fun getFingerprint(musicFile: File): String?

    /**
     * Retrieves information about a music file.
     * If [shouldSearchForMetadata] is true, the primary source of information will be a remote service (Acoustid).
     */
    suspend fun getInformationAboutMusicFile(
        musicFile: File,
        customMetadata: CustomMusicMetadata?,
        shouldSearchForMetadata: Boolean,
    ): Metadata

    /**
     * Music metadata from a file our a remote source.
     */
    data class Metadata(
        val name: String,
        val artists: List<String>,
        val album: String,
        val coverPath: String?,
        val fingerprint: String,
        val duration: Long,
    )
}