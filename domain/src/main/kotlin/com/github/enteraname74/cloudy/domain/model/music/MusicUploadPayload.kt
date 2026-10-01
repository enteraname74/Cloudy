package com.github.enteraname74.cloudy.domain.model.music

import com.github.enteraname74.cloudy.domain.model.FileData

data class MusicUploadPayload(
    val musicFile: FileData,
    val musicCover: FileData?,
    val spec: MusicUploadSpec,
)