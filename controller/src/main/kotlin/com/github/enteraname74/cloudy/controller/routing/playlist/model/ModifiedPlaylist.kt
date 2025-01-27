package com.github.enteraname74.cloudy.controller.routing.playlist.model

import com.github.enteraname74.cloudy.domain.model.Playlist
import com.github.enteraname74.cloudy.domain.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class ModifiedPlaylist(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = false,
)

fun Playlist.fromModifiedPlaylist(modifiedPlaylist: ModifiedPlaylist): Playlist =
    this.copy(
        id = modifiedPlaylist.id,
        name = modifiedPlaylist.name,
        nbPlayed = modifiedPlaylist.nbPlayed,
        isInQuickAccess = modifiedPlaylist.isInQuickAccess,
        lastUpdateAt = LocalDateTime.now(),
    )
