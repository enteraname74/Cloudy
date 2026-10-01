package com.github.enteraname74.cloudy.domain.model

import com.github.enteraname74.cloudy.domain.model.music.MusicId
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.uuid.Uuid

@Serializable
data class ListeningStatistics(
    val id: String,
    val userId: Uuid,
    val nbPlayed: Int,
    val timeListened: Duration?,
    val localMonthYear: LocalMonthYear,
    val musicId: MusicId?,
    val playlistId: Uuid?,
    val albumId: Uuid?,
    val artistId: Uuid?,
    override val lastUpdateAtMillis: Long,
) : UpdatableElement