package com.github.enteraname74.cloudy.domain.model.playlist

import com.github.enteraname74.cloudy.domain.model.UpdatableElement
import com.github.enteraname74.cloudy.domain.util.DateUtils
import kotlinx.serialization.Serializable
import kotlin.math.max
import kotlin.uuid.Uuid

@Serializable
data class Playlist(
    val id: Uuid = Uuid.random(),
    val userId: Uuid,
    val name: String,
    val coverPath: String?,
    val isFavorite: Boolean = false,
    val addedDateMillis: Long = DateUtils.now(),
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = false,
    override val lastUpdateAtMillis: Long = DateUtils.now(),
): UpdatableElement {

    fun merge(
        playlistUpload: PlaylistUpload,
    ): Playlist = copy(
        name = playlistUpload.name,
        isFavorite = playlistUpload.isFavorite,
        isInQuickAccess = playlistUpload.isInQuickAccess,
        nbPlayed = max(nbPlayed, playlistUpload.nbPlayed),
    )

    companion object {
        const val COVER_PATH = "playlist/cover/"
    }
}