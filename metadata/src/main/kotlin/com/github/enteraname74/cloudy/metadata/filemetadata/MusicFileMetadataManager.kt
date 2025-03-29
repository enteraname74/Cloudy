package com.github.enteraname74.cloudy.metadata.filemetadata

import com.github.enteraname74.cloudy.domain.model.Music
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.logging.CloudyLogger
import com.github.enteraname74.cloudy.metadata.model.MusicMetadata
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.images.ArtworkFactory
import java.io.File

class MusicFileMetadataManager {
    private val logger = CloudyLogger(this::class)

    fun getMusicFileCover(musicFile: File): ByteArray? =
        runCatching {
            AudioFileIO
                .read(musicFile)
                .tag
                .firstArtwork
                .binaryData
        }.getOrNull()

    fun getMetadataOfFile(musicFile: File): MusicMetadata =
        try {
            val audioFile = AudioFileIO.read(musicFile)
            val tag = audioFile.tag

            MusicMetadata(
                name = tag.getFirst(FieldKey.TITLE),
                artist = tag.getFirst(FieldKey.ARTIST),
                album = tag.getFirst(FieldKey.ALBUM),
                duration = audioFile.audioHeader.trackLength.toLong(),
            ).replaceBlank()
        } catch (e: Exception) {
            logger.error("Failed to retrieve metadata of file ${musicFile.name} with error ${e.message}")
            MusicMetadata.unknownMusicMetadata()
        }

    fun setMetadataOfFile(
        musicFile: File,
        music: Music,
        cover: ByteArray?,
    ): CloudyResult<Unit> =
        try {
            val audioFile = AudioFileIO.read(musicFile)
            val tag = audioFile.tag

            tag.setField(FieldKey.TITLE, music.name)
            tag.setField(FieldKey.ALBUM, music.album)
            tag.setField(FieldKey.ARTIST, music.artist)

            cover?.let { currentArtwork ->
                try {
                    val artwork = ArtworkFactory.getNew()
                    tag.deleteArtworkField()
                    artwork.binaryData = currentArtwork
                    tag.setField(artwork)
                } catch (e: Exception) {
                    logger.error("Exception while writing cover: ${e.localizedMessage}")
                }
            }

            audioFile.commit()

            CloudyResult.Success(Unit)
        } catch (e: Exception) {
            logger.error("Failed to save metadata of file ${musicFile.name} with error ${e.localizedMessage}")
            CloudyResult.Error()
        }
}