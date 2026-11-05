package com.github.enteraname74.cloudy.domain.model.music

import com.github.enteraname74.cloudy.domain.model.FileData

data class MusicUploadPayload(
    val musicFile: FileData,
    val musicCover: FileData?,
    /**
     * Information about the music given by the client.
     * If null, the information is read from the music file itself.
     */
    val spec: MusicUploadSpec?,
)