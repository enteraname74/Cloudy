package com.github.enteraname74.cloudy.controller.routing.playlist.model

import com.github.enteraname74.cloudy.domain.model.Playlist
import com.github.enteraname74.cloudy.domain.util.DateUtils
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class ModifiedPlaylist(
    val id: Uuid,
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
        lastUpdateAtMillis = DateUtils.now(),
    )
