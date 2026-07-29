package com.github.enteraname74.cloudy.controller.routing.player.model

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class UpdateCurrentMusicBody(
    val listId: Uuid,
    val deviceId: String,
    val musicId: String,
) {
    fun isValid(): Boolean =
        musicId.isNotBlank() && deviceId.isNotBlank()
}
