package com.github.enteraname74.cloudy.domain.model

import com.github.enteraname74.cloudy.domain.serializer.LocalDateTimeSerializer
import com.github.enteraname74.cloudy.domain.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class MusicPlaylist(
    @Serializable(with = UUIDSerializer::class)
    val musicId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val playlistId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID,
    @Serializable(with = LocalDateTimeSerializer::class)
    override val lastUpdateAt: LocalDateTime
): UpdatableElement {
    val id: String
        get() = "$musicId:$playlistId"
}
