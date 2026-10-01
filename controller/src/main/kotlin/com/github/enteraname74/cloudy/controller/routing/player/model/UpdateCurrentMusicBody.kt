package com.github.enteraname74.cloudy.controller.routing.player.model

import com.github.enteraname74.cloudy.domain.model.music.MusicId
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class UpdateCurrentMusicBody(
    val listId: Uuid,
    val deviceId: String,
    val musicId: MusicId,
) {
    fun isValid(): Boolean =
        musicId.raw.isNotBlank() && deviceId.isNotBlank()
}
