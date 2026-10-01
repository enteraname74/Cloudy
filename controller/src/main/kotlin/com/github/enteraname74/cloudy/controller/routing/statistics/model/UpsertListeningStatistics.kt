package com.github.enteraname74.cloudy.controller.routing.statistics.model

import com.github.enteraname74.cloudy.domain.model.ListeningStatistics
import com.github.enteraname74.cloudy.domain.model.LocalMonthYear
import com.github.enteraname74.cloudy.domain.model.UpdatableElement
import com.github.enteraname74.cloudy.domain.model.music.MusicId
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.uuid.Uuid

@Serializable
data class UpsertListeningStatistics(
    val id: String,
    val nbPlayed: Int,
    val timeListened: Duration?,
    val localMonthYear: LocalMonthYear,
    val musicId: MusicId?,
    val playlistId: Uuid?,
    val albumId: Uuid?,
    val artistId: Uuid?,
    override val lastUpdateAtMillis: Long,
) : UpdatableElement {
    fun toListeningStatistics(
        userId: Uuid
    ): ListeningStatistics =
        ListeningStatistics(
            id = id,
            userId = userId,
            nbPlayed = nbPlayed,
            timeListened = timeListened,
            localMonthYear = localMonthYear,
            musicId = musicId,
            playlistId = playlistId,
            albumId = albumId,
            artistId = artistId,
            lastUpdateAtMillis = lastUpdateAtMillis,
        )
}
