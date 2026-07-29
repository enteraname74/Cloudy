package com.github.enteraname74.cloudy.controller.routing.player.model

import com.github.enteraname74.cloudy.domain.model.player.PlayedList
import com.github.enteraname74.cloudy.domain.model.player.PlayedListUpdate
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class UpdatePlayedListBody(
    val listId: Uuid,
    val deviceId: String,
    val state: PlayedList.State,
) {
    fun isValid(): Boolean =
        deviceId.isNotBlank()

    fun toPlayedListUpdate(): PlayedListUpdate =
        PlayedListUpdate(
            listId = listId,
            state = state,
        )
}
