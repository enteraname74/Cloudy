package com.github.enteraname74.cloudy.metadata.filemetadata

import com.github.enteraname74.cloudy.domain.model.music.Music
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

            val albumArtist = tag.getFirst(FieldKey.ALBUM_ARTIST)?.takeIf { it.isNotBlank() }
            val artist = tag.getFirst(FieldKey.ARTIST)

            val artists: List<MusicMetadata.Artist> = buildList {
                if (albumArtist != null && albumArtist != artist) {
                    add(MusicMetadata.Artist(name = albumArtist))
                }
                add(MusicMetadata.Artist(name = artist))
            }

            MusicMetadata(
                name = tag.getFirst(FieldKey.TITLE),
                artists = artists,
                album = MusicMetadata.Album(
                    name = tag.getFirst(FieldKey.ALBUM),
                    artist = artists.firstOrNull() ?: MusicMetadata.unknownArtist(),
                    ),
                duration = audioFile.audioHeader.trackLength.toLong(),
                albumPosition = tag.getFirst(FieldKey.TRACK)?.toIntOrNull(),
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
            tag.setField(FieldKey.ALBUM, music.album.name)
            tag.setField(FieldKey.ARTIST, music.artists.joinToString { it.name })

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