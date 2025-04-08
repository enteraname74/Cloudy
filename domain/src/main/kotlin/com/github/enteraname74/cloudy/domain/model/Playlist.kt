package com.github.enteraname74.cloudy.domain.model

import com.github.enteraname74.cloudy.domain.serializer.LocalDateTimeSerializer
import com.github.enteraname74.cloudy.domain.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class Playlist(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID,
    val name: String,
    val coverPath: String?,
    val isFavorite: Boolean = false,
    @Serializable(with = LocalDateTimeSerializer::class)
    val addedDate: LocalDateTime = LocalDateTime.now(),
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = false,
    @Serializable(with = LocalDateTimeSerializer::class)
    override val lastUpdateAt: LocalDateTime = LocalDateTime.now()
): UpdatableElement {
    companion object {
        const val COVER_PATH = "playlist/cover/"
    }
}
