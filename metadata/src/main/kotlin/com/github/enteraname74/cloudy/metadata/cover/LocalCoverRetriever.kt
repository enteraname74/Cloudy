package com.github.enteraname74.cloudy.metadata.cover

import org.jaudiotagger.audio.AudioFileIO
import java.io.File

class LocalCoverRetriever {
    fun getMusicFileCover(file: File): ByteArray? =
        runCatching {
            AudioFileIO
                .read(file)
                .tag
                .firstArtwork
                .binaryData
        }.getOrNull()
}