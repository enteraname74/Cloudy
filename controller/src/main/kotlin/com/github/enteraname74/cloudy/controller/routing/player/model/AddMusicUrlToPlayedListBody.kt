package com.github.enteraname74.cloudy.controller.routing.player.model

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class AddMusicUrlToPlayedListBody(
    val deviceId: String,
    val listId: Uuid,
    val url: String,
) {
    fun isValid(): Boolean =
        deviceId.isNotBlank() && url.isNotBlank()
}
