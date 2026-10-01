package com.github.enteraname74.cloudy.domain.model.player

import io.ktor.server.websocket.DefaultWebSocketServerSession
import kotlin.uuid.Uuid

data class PlayerSocketUser(
    val listId: Uuid,
    val userId: Uuid,
    val deviceId: String,
    val session: DefaultWebSocketServerSession,
)
