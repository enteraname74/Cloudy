package com.github.enteraname74.cloudy.metadata.filemetadata

import com.github.enteraname74.cloudy.metadata.model.MusicMetadata
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File

internal class MusicFileMetadataManager {
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
        } catch (_: Exception) {
            MusicMetadata.unknownMusicMetadata()
        }
}