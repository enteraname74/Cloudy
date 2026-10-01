package com.github.enteraname74.cloudy.domain.model.player

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class PlayedListUpdate(
    val listId: Uuid,
    val state: PlayedList.State,
)
