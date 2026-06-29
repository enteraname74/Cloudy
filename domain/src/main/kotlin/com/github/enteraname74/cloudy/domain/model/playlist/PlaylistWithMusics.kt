package com.github.enteraname74.cloudy.domain.model.playlist

import kotlinx.serialization.Serializable

@Serializable
data class PlaylistWithMusics(
    val playlist: Playlist,
    val musicIds: List<String>,
)
