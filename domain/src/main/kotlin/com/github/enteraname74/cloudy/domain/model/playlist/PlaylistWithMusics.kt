package com.github.enteraname74.cloudy.domain.model.playlist

import com.github.enteraname74.cloudy.domain.model.music.MusicId
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistWithMusics(
    val playlist: Playlist,
    val musicIds: List<MusicId>,
)
