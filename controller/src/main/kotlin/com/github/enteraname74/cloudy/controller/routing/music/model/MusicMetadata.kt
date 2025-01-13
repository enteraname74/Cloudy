package com.github.enteraname74.cloudy.controller.routing.music.model

import kotlinx.serialization.Serializable

@Serializable
data class MusicMetadata(
    val name: String? = null,
    val album: String? = null,
    val artists: List<String> = emptyList(),
)
