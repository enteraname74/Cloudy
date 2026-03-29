package com.github.enteraname74.cloudy.controller.routing.player.model

import kotlinx.serialization.Serializable

@Serializable
data class JoinPlayedListBody(
    val code: String,
    val deviceId: String,
) {
    fun isValid(): Boolean =
        code.isNotBlank() && deviceId.isNotBlank()
}
