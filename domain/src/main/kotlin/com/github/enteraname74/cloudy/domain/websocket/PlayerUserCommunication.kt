package com.github.enteraname74.cloudy.domain.websocket

import com.github.enteraname74.cloudy.domain.model.player.PlayerSocketUser
import com.github.enteraname74.cloudy.logging.CloudyLogger
import io.ktor.server.websocket.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap
import kotlin.uuid.Uuid

class PlayerUserCommunication {
    private val logger = CloudyLogger(this::class)
    private val connections = ConcurrentHashMap<Uuid, MutableSet<PlayerSocketUser>>()
    private val mutex = Mutex()

    suspend fun add(connection: PlayerSocketUser) {
        mutex.withLock {
            val set = connections.getOrPut(connection.listId) { mutableSetOf() }
            set.removeIf { it.userId == connection.userId && it.deviceId == connection.deviceId }
            set += connection
        }
    }

    suspend fun remove(listId: Uuid, userId: Uuid, deviceId: String) {
        mutex.withLock {
            val set = connections[listId] ?: return
            set.removeIf { it.userId == userId && it.deviceId == deviceId }
            if (set.isEmpty()) {
                connections.remove(listId)
            }
        }
    }

    suspend fun broadcastEvent(
        listId: Uuid,
        event: Event,
        exceptDeviceId: String? = null,
    ) {
        val targets = connections[listId]?.toList().orEmpty()
        targets.forEach { connection ->
            if (connection.deviceId != exceptDeviceId) {
                runCatching {
                    connection.session.sendSerialized(event)
                }.getOrElse {
                    logger.error("Couldn't send message through socket: $it")
                }
            }
        }
    }

    enum class Event {
        SyncMusics,
        SyncPlayedList,
        SyncUsers,
        PlayedListDeleted,
    }
}