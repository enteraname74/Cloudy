package com.github.enteraname74.cloudy.domain.filepersistence

import com.github.enteraname74.cloudy.domain.model.CustomMusicMetadata
import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.FingerprintData
import com.github.enteraname74.cloudy.domain.model.music.MusicUploadSpec
import java.io.File

interface MusicInformationRetriever {

    suspend fun getFingerprint(musicFile: File): FingerprintData?

    /**
     * Retrieves information about a music file.
     * If [shouldSearchForMetadata] is true, the primary source of information will be a remote service (Acoustid).
     */
    suspend fun getMetadataOfFile(
        musicFile: File,
        musicUploadSpec: MusicUploadSpec,
        fingerprintData: FingerprintData?,
        shouldSearchForMetadata: Boolean,
    ): Metadata

    /**
     * Music metadata from a file or a remote source.
     */
    data class Metadata(
        val uploadSpec: MusicUploadSpec,
        val fingerprint: String,
        // Cover found on a remote service, to save like an uploaded cover.
        val remoteCover: FileData? = null,
    )
}