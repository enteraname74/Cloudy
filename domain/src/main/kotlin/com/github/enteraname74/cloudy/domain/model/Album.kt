package com.github.enteraname74.cloudy.domain.model

import com.github.enteraname74.cloudy.domain.util.DateUtils
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class Album(
    val id: Uuid,
    val userId: Uuid,
    val name: String,
    val coverPath: String?,
    val addedDateMillis: Long,
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = false,
    val artist: Artist,
    override val lastUpdateAtMillis: Long = DateUtils.now(),
) : UpdatableElement {

    fun merge(
        albumUpload: AlbumUpload,
        artist: Artist,
    ) : Album =
        copy(
            name = albumUpload.name,
            isInQuickAccess = albumUpload.isInQuickAccess,
            nbPlayed = albumUpload.nbPlayed,
            artist = artist,
        )

    companion object {
        const val COVER_PATH = "album/cover/"
    }
}

@Serializable
data class AlbumUpload(
    val name: String,
    val nbPlayed: Int,
    val isInQuickAccess: Boolean,
    val artistUpload: ArtistUpload,
) {
    fun toNewAlbum(
        artist: Artist,
        userId: Uuid,
    ): Album =
        Album(
            id = Uuid.random(),
            userId = userId,
            name = name,
            coverPath = null,
            addedDateMillis = DateUtils.now(),
            nbPlayed = nbPlayed,
            isInQuickAccess = isInQuickAccess,
            artist = artist,
        )
}