package com.github.enteraname74.cloudy.controller.routing.playlist.model

import com.github.enteraname74.cloudy.domain.model.Playlist
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class PlaylistCreation(
    val name: String
) {
    fun toNewPlaylist(
        userId: Uuid,
    ): Playlist =
        Playlist(
            id = Uuid.random(),
            name = name,
            userId = userId,
            coverPath = null,
        )
}