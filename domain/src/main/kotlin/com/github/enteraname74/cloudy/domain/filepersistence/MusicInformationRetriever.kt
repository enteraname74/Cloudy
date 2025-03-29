package com.github.enteraname74.cloudy.domain.filepersistence

import com.github.enteraname74.cloudy.domain.ext.joinArtists
import com.github.enteraname74.cloudy.domain.model.CustomMusicMetadata
import com.github.enteraname74.cloudy.domain.model.Music
import java.io.File
import java.util.*

interface MusicInformationRetriever {

    /**
     * Retrieves information about a music file.
     * If [shouldSearchForMetadata] is true, the primary source of information will be a remote service (Acoustid).
     */
    suspend fun getInformationAboutMusicFile(
        musicFile: File,
        musicId: UUID,
        customMetadata: CustomMusicMetadata?,
        shouldSearchForMetadata: Boolean,
    ): Metadata

    /**
     * Music metadata from a file our a remote source.
     */
    data class Metadata(
        val musicId: UUID,
        val name: String,
        val artists: List<String>,
        val album: String,
        val coverPath: String?,
        val fingerprint: String,
        val duration: Long,
    )
}

fun Music.updateFromMetadata(metadata: MusicInformationRetriever.Metadata): Music =
    this.copy(
        name = name,
        id = metadata.musicId,
        artist = metadata.artists.joinArtists(),
        album = metadata.album,
        coverPath = metadata.coverPath,
        fingerprint = metadata.fingerprint,
        duration = metadata.duration,
    )