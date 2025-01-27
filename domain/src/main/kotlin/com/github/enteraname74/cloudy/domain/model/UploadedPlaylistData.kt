package com.github.enteraname74.cloudy.domain.model

import com.github.enteraname74.cloudy.domain.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class UploadedPlaylistData(
    val playlist: Playlist,
    @Serializable(with = UUIDSerializer::class)
    val userPlaylistId: UUID,
)
