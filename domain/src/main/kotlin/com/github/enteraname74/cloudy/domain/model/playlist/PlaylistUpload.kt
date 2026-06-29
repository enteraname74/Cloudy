package com.github.enteraname74.cloudy.domain.model.playlist

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class PlaylistUpload(
    val name: String,
    val isFavorite: Boolean,
    val nbPlayed: Int,
    val isInQuickAccess: Boolean,
    val musicIds: List<String>,
) {

    fun isValid(): Boolean =
        name.isNotBlank() && nbPlayed >= 0 && musicIds.all { it.isNotBlank() }
    fun toNewPlaylist(
        userId: Uuid,
    ): Playlist =
        Playlist(
            userId = userId,
            name = name,
            coverPath = null,
        )
}
