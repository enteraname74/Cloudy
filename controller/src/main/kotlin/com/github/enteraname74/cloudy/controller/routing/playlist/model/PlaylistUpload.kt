package com.github.enteraname74.cloudy.controller.routing.playlist.model

import com.github.enteraname74.cloudy.domain.model.Playlist
import com.github.enteraname74.cloudy.domain.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class PlaylistUpload(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val isFavorite: Boolean,
    val isInQuickAccess: Boolean,
) {
    fun toPlaylist(
        userId: UUID,
    ): Playlist = Playlist(
        id = id,
        name = name,
        isFavorite = isFavorite,
        isInQuickAccess = isInQuickAccess,
        userId = userId,
        coverPath = null,
    )
}
