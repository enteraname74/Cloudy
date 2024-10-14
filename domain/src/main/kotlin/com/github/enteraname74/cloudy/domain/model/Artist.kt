package com.github.enteraname74.cloudy.domain.model

import com.github.enteraname74.cloudy.domain.serializer.LocalDateTimeSerializer
import com.github.enteraname74.cloudy.domain.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class Artist(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID(),
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID,
    val name: String,
    val coverPath: String?,
    @Serializable(with = LocalDateTimeSerializer::class)
    var addedDate: LocalDateTime = LocalDateTime.now(),
    var nbPlayed: Int = 0,
    var isInQuickAccess: Boolean = false,
    @Serializable(with = LocalDateTimeSerializer::class)
    override val lastUpdateAt: LocalDateTime = LocalDateTime.now(),
): UpdatableElement
