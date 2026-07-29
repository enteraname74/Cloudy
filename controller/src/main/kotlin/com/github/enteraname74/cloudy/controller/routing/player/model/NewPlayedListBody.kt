package com.github.enteraname74.cloudy.controller.routing.player.model

import kotlinx.serialization.Serializable

@Serializable
data class NewPlayedListBody(
    val deviceId: String,
    val musicIds: List<String>,
) {
    fun isValid(): Boolean =
        deviceId.isNotBlank() && musicIds.isNotEmpty()
}
