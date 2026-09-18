package com.github.enteraname74.cloudy.domain.model.player

import com.github.enteraname74.cloudy.domain.model.music.Music
import com.github.enteraname74.cloudy.domain.model.music.MusicId
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class PlayerMusic(
    val playedListId: Uuid,
    val music: Music,
    val order: Double,
    val lastPlayedMillis: Long?,
) {
    val id: String = "$playedListId-${music.id.raw}"
}

data class SimplePlayerMusic(
    val playedListId: Uuid,
    val musicId: MusicId,
    val order: Double,
    val lastPlayedMillis: Long?,
) {
    val id: String = "$playedListId-${musicId.raw}"
}
