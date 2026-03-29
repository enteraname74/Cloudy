package com.github.enteraname74.cloudy.domain.model.player

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class PlayedList(
    val id: Uuid,
    val inviteCode: String,
    val state: State,
    val owner: PlayerUser?,
    val users: List<PlayerUser>,
) {
    fun isEmpty(): Boolean =
        users.isEmpty()

    enum class State(val value: String) {
        Playing("playing"),
        Paused("paused");

        companion object {
            fun fromValueOrPaused(value: String): State =
                entries.find { it.value == value } ?: Paused

            fun fromValue(value: String): State? =
                entries.find { it.value == value }
        }
    }
}
