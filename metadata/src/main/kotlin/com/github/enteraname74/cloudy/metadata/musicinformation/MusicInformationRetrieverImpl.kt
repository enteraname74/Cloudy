package com.github.enteraname74.cloudy.metadata.musicinformation

import com.github.enteraname74.cloudy.domain.filepersistence.MusicInformationRetriever
import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.FingerprintData
import com.github.enteraname74.cloudy.domain.model.music.MusicUploadSpec
import com.github.enteraname74.cloudy.metadata.acoustid.AcoustidApiClient
import com.github.enteraname74.cloudy.metadata.cover.CoverArtArchiveClient
import com.github.enteraname74.cloudy.metadata.filemetadata.MusicFileMetadataManager
import com.github.enteraname74.cloudy.metadata.fingerprint.FingerprintRetriever
import com.github.enteraname74.cloudy.metadata.model.MusicMetadata
import java.io.File
import java.security.MessageDigest

class MusicInformationRetrieverImpl : MusicInformationRetriever {
    private val metadataManager = MusicFileMetadataManager()
    private val fingerprintRetriever = FingerprintRetriever()
    private val coverArtArchiveClient = CoverArtArchiveClient()
    private val acoustidClient = AcoustidApiClient()

    override suspend fun getFingerprint(musicFile: File): FingerprintData? =
        fingerprintRetriever
            .getFingerprintFromMusic(musicPath = musicFile.path)

    override suspend fun getMetadataOfFile(
        musicFile: File,
        musicUploadSpec: MusicUploadSpec?,
        fingerprintData: FingerprintData?,
        shouldSearchForMetadata: Boolean,
    ): MusicInformationRetriever.Metadata {
        // File metadata keeps some info from the music upload spec if any.
        val fileMetadata: MusicMetadata = metadataManager.getMetadataOfFile(musicFile = musicFile).let { metadata ->
            musicUploadSpec?.let { spec ->
                metadata.updateNbPlayedAndQuickAccessFromOther(MusicMetadata.fromUploadSpec(spec))
            } ?: metadata
        }

        // TODO V2: Handle duration missing for OPUS format (JaudioTagger crashing)
        return if (!shouldSearchForMetadata || fingerprintData == null) {
            MusicInformationRetriever.Metadata(
                uploadSpec = musicUploadSpec ?: fileMetadata.toMusicUpload(),
                fingerprint = fingerprintData?.fingerprint?.hashed() ?: fileMetadata.name,
            )
        } else {
            val finalMetadata = acoustidClient.getMusicMetadata(
                fingerprintData = fingerprintData,
                fileMetadata = fileMetadata,
            ).replaceBlank()

            val remoteCover: FileData? = finalMetadata.releaseGroupId?.let { releaseGroupId ->
                coverArtArchiveClient.getReleaseGroupCover(releaseGroupId = releaseGroupId)
            }

            MusicInformationRetriever.Metadata(
                uploadSpec = finalMetadata.toMusicUpload(),
                fingerprint = fingerprintData.fingerprint.hashed(),
                remoteCover = remoteCover,
            )
        }
    }

    private fun ByteArray.toHex() =
        joinToString("") { "%02x".format(it) }

    private fun String.hashed(): String =
        MessageDigest
            .getInstance("MD5")
            .digest(this.toByteArray(Charsets.UTF_8))
            .toHex()
}
