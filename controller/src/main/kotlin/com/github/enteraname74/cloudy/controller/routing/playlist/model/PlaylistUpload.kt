package com.github.enteraname74.cloudy.controller.routing.playlist.model

import com.github.enteraname74.cloudy.domain.model.Playlist
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class PlaylistUpload(
    val id: Uuid,
    val name: String,
    val isFavorite: Boolean,
    val isInQuickAccess: Boolean,
) {
    fun toPlaylist(
        userId: Uuid,
    ): Playlist = Playlist(
        id = id,
        name = name,
        isFavorite = isFavorite,
        isInQuickAccess = isInQuickAccess,
        userId = userId,
        coverPath = null,
    )
}
