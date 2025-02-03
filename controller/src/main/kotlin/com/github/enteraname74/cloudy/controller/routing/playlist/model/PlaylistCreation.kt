package com.github.enteraname74.cloudy.controller.routing.playlist.model

import com.github.enteraname74.cloudy.domain.model.Playlist
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class PlaylistCreation(
    val name: String
) {
    fun toNewPlaylist(
        userId: UUID,
    ): Playlist =
        Playlist(
            id = UUID.randomUUID(),
            name = name,
            userId = userId,
            coverPath = null,
        )
}