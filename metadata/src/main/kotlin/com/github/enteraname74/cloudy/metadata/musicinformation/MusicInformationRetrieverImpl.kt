package com.github.enteraname74.cloudy.metadata.musicinformation

import com.github.enteraname74.cloudy.domain.filepersistence.MusicInformationResult
import com.github.enteraname74.cloudy.domain.filepersistence.MusicInformationRetriever
import com.github.enteraname74.cloudy.domain.model.CustomMusicMetadata
import com.github.enteraname74.cloudy.metadata.acoustid.AcoustidApiClient
import com.github.enteraname74.cloudy.metadata.cover.RemoteMusicCoverRetriever
import com.github.enteraname74.cloudy.metadata.filemetadata.MusicFileMetadataManager
import com.github.enteraname74.cloudy.metadata.fingerprint.FingerprintData
import com.github.enteraname74.cloudy.metadata.fingerprint.FingerprintRetriever
import com.github.enteraname74.cloudy.metadata.model.MusicMetadata
import java.io.File
import java.security.MessageDigest
import java.util.*

class MusicInformationRetrieverImpl : MusicInformationRetriever {
    private val metadataManager = MusicFileMetadataManager()
    private val fingerprintRetriever = FingerprintRetriever()
    private val remoteMusicCoverRetriever = RemoteMusicCoverRetriever()

    override suspend fun getInformationAboutMusicFile(
        musicFile: File,
        musicId: UUID,
        customMetadata: CustomMusicMetadata?,
        shouldSearchForMetadata: Boolean,
    ): MusicInformationResult {
        val fileMetadata: MusicMetadata = metadataManager.getMetadataOfFile(musicFile = musicFile)

        val fingerprintData: FingerprintData? = fingerprintRetriever
            .getFingerprintFromMusic(musicPath = musicFile.path)

        // TODO: Handle duration missing for OPUS format (JaudioTagger crashing)
        if (!shouldSearchForMetadata || fingerprintData == null) {
            return MusicInformationResult.FileMetadata(
                name = customMetadata?.name ?: fileMetadata.name,
                artists = customMetadata?.artists?.takeIf { it.isNotEmpty() } ?: listOf(fileMetadata.artist),
                album = customMetadata?.album ?: fileMetadata.album,
                fingerprint = fingerprintData?.fingerprint?.hashed() ?: fileMetadata.name,
                coverPath = coverUrlFromFile(musicId),
                duration = customMetadata?.duration ?: fileMetadata.duration,
                musicId = musicId,
            )
        } else {
            val acoustidClient = AcoustidApiClient()
            val finalMetadata = acoustidClient.getMusicMetadata(
                fingerprintData = fingerprintData,
                fileMetadata = fileMetadata
            ).replaceBlank()

            val coverPath: String? = remoteMusicCoverRetriever.getCoverURL(
                musicName = finalMetadata.name,
                musicArtist = finalMetadata.artist,
            )

            return MusicInformationResult.FileMetadata(
                name = finalMetadata.name,
                artists = listOf(finalMetadata.artist),
                album = finalMetadata.album,
                fingerprint = fingerprintData.fingerprint.hashed(),
                coverPath = coverPath ?: coverUrlFromFile(musicId),
                duration = finalMetadata.duration,
                musicId = musicId,
            )
        }
    }

    private fun coverUrlFromFile(musicId: UUID): String =
        "music/cover/$musicId"

    private fun ByteArray.toHex() =
        joinToString("") { "%02x".format(it) }

    private fun String.hashed(): String =
        MessageDigest
            .getInstance("MD5")
            .digest(this.toByteArray(Charsets.UTF_8))
            .toHex()
}
