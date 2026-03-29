package com.github.enteraname74.cloudy.domain.model.player

import com.github.enteraname74.cloudy.domain.model.music.Music
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class PlayerMusic(
    val playedListId: Uuid,
    val music: Music,
    val order: Double,
    val lastPlayedMillis: Long?,
) {
    val id = "$playedListId-${music.fingerprint}"
}

data class SimplePlayerMusic(
    val playedListId: Uuid,
    val musicId: String,
    val order: Double,
    val lastPlayedMillis: Long?,
) {
    val id = "$playedListId-$musicId"
}
