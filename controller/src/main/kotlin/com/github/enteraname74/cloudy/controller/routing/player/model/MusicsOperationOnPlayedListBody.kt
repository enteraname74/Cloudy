package com.github.enteraname74.cloudy.controller.routing.player.model

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class MusicsOperationOnPlayedListBody(
    val deviceId: String,
    val listId: Uuid,
    val musicIds: List<String>,
) {
    fun isValid(): Boolean =
        deviceId.isNotBlank()
}
