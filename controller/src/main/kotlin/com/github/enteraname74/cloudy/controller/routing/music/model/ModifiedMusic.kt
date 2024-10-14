package com.github.enteraname74.cloudy.controller.routing.music.model

import com.github.enteraname74.cloudy.domain.model.Music
import com.github.enteraname74.cloudy.domain.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.*

@Serializable
data class ModifiedMusic(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val album: String,
    val artist: String,
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = false,
)

fun Music.fromModifiedMusic(modifiedMusic: ModifiedMusic): Music =
    this.copy(
        id = modifiedMusic.id,
        name = modifiedMusic.name,
        album = modifiedMusic.album,
        artist = modifiedMusic.artist,
        nbPlayed = modifiedMusic.nbPlayed,
        isInQuickAccess = modifiedMusic.isInQuickAccess,
        lastUpdateAt = LocalDateTime.now(),
    )
