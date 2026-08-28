package com.github.enteraname74.cloudy.controller.routing.player.model

import com.github.enteraname74.cloudy.domain.model.music.Music
import com.github.enteraname74.cloudy.domain.model.music.MusicId
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class CheckPlayerMusicIdsBody(
    val musicIds: List<MusicId>,
    val deviceId: String,
    val listId: Uuid,
) {
    fun isValid(): Boolean =
        deviceId.isNotBlank()
}
