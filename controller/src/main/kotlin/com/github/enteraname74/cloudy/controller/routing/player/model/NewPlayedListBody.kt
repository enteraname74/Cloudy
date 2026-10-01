package com.github.enteraname74.cloudy.controller.routing.player.model

import com.github.enteraname74.cloudy.domain.model.music.MusicId
import kotlinx.serialization.Serializable

@Serializable
data class NewPlayedListBody(
    val deviceId: String,
    val musicIds: List<MusicId>,
) {
    fun isValid(): Boolean =
        deviceId.isNotBlank() && musicIds.isNotEmpty()
}
