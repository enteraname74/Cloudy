package com.github.enteraname74.cloudy.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UploadedMusicData(
    val music: Music,
    val album: Album,
    val artists: List<Artist>,
)
