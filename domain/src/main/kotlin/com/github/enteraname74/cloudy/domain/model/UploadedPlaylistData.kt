package com.github.enteraname74.cloudy.domain.model

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class UploadedPlaylistData(
    val playlist: Playlist,
    val userPlaylistId: Uuid,
)
