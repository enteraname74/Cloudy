package com.github.enteraname74.cloudy.domain.model.player

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class PlayerUser(
    val id: Uuid,
    val deviceId: String,
    val username: String,
    val joinedAt: Long,
)