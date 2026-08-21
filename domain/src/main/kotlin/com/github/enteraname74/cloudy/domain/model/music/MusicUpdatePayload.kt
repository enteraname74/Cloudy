package com.github.enteraname74.cloudy.domain.model.music

import com.github.enteraname74.cloudy.domain.model.FileData

data class MusicUpdatePayload(
    val spec: MusicUpdateSpec,
    val musicCover: FileData?,
)
