package com.github.enteraname74.cloudy.controller.routing.player.model

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class CheckPlayerMusicIdsBody(
    val musicIds: List<String>,
    val deviceId: String,
    val listId: Uuid,
) {
    fun isValid(): Boolean =
        deviceId.isNotBlank()
}
