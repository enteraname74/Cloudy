package com.github.enteraname74.cloudy.controller.routing.player.model

import com.github.enteraname74.cloudy.domain.model.music.MusicId
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class MusicsOperationOnPlayedListBody(
    val deviceId: String,
    val listId: Uuid,
    val musicIds: List<MusicId>,
) {
    fun isValid(): Boolean =
        deviceId.isNotBlank()
}
