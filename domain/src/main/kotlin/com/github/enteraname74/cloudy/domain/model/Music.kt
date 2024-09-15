package com.github.enteraname74.cloudy.domain.model

import com.github.enteraname74.cloudy.domain.serializer.LocalDateTimeSerializer
import com.github.enteraname74.cloudy.domain.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class Music(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID(),
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID,
    val name: String,
    val album: String,
    val artist: String,
    val path: String,
    val coverPath: String?,
    val duration: Long,
    @Serializable(with = LocalDateTimeSerializer::class)
    val addedDate: LocalDateTime = LocalDateTime.now(),
    @Serializable(with = LocalDateTimeSerializer::class)
    val lastUpdatedAt: LocalDateTime = LocalDateTime.now(),
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = false,
    @Serializable(with = UUIDSerializer::class)
    val albumId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val artistId: UUID,
    val fingerprint: String,
)
