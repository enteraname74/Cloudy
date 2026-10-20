package com.github.enteraname74.cloudy.domain.model.album

import com.github.enteraname74.cloudy.domain.model.UpdatableElement
import com.github.enteraname74.cloudy.domain.model.artist.Artist
import com.github.enteraname74.cloudy.domain.util.DateUtils
import kotlinx.serialization.Serializable
import kotlin.math.max
import kotlin.uuid.Uuid

@Serializable
data class Album(
    val id: Uuid = Uuid.random(),
    val userId: Uuid,
    val name: String,
    val coverPath: String?,
    val artist: Artist,
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = false,
    val addedDateMillis: Long = DateUtils.now(),
    override val lastUpdateAtMillis: Long = DateUtils.now(),
) : UpdatableElement {

    fun merge(
        albumUpload: AlbumUpload,
        artist: Artist,
    ): Album =
        copy(
            name = albumUpload.name,
            isInQuickAccess = albumUpload.isInQuickAccess,
            nbPlayed = max(albumUpload.nbPlayed, nbPlayed),
            artist = artist,
        )

    fun merge(
        albumUpdate: AlbumUpdate,
        artist: Artist,
    ): Album =
        copy(
            name = albumUpdate.name,
            isInQuickAccess = albumUpdate.isInQuickAccess,
            nbPlayed = max(albumUpdate.nbPlayed, nbPlayed),
            artist = artist,
        )

    companion object {
        const val COVER_PATH: String = "album/cover/"
    }
}