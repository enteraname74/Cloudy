package com.github.enteraname74.cloudy.controller.routing.player.model

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class RemoveUserFromPlayedListBody(
    val listId: Uuid,
    val deviceId: String,
    val userIdToRemove: Uuid,
    val deviceIdToRemove: String,
) {
    fun isValid(): Boolean =
        deviceId.isNotBlank() && deviceIdToRemove.isNotBlank()
}
