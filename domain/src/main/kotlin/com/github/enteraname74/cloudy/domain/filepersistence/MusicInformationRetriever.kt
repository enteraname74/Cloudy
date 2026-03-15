package com.github.enteraname74.cloudy.domain.filepersistence

import com.github.enteraname74.cloudy.domain.model.Album
import com.github.enteraname74.cloudy.domain.model.Artist
import com.github.enteraname74.cloudy.domain.model.CustomMusicMetadata
import com.github.enteraname74.cloudy.domain.model.Music
import com.github.enteraname74.cloudy.domain.util.DateUtils
import java.io.File
import kotlin.uuid.Uuid

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

// TODO: Improve MusicInformationRetriever.Metadata to include album artist
// TODO: Improve artist check with existing ones.
@Deprecated("Not useful with new data structure")
fun Music.updateFromMetadata(metadata: MusicInformationRetriever.Metadata): Music {
    val artists = metadata.artists.map {
        Artist(
            id = Uuid.random(),
            userId = userId,
            name = it,
            coverPath = null,
            addedDateMillis = DateUtils.now(),
        )
    }

    return copy(
        name = name,
        fingerprint = metadata.fingerprint,
        artists = artists,
        album = Album(
            id = Uuid.random(),
            userId = userId,
            name = metadata.album,
            coverPath = null,
            addedDateMillis = addedDateMillis,
            artist = artists.first(),
        ),
        coverPath = metadata.coverPath,
        duration = metadata.duration,
    )
}