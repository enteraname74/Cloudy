package com.github.enteraname74.cloudy.domain.model.album

import com.github.enteraname74.cloudy.domain.model.artist.Artist
import com.github.enteraname74.cloudy.domain.model.artist.ArtistUpload
import com.github.enteraname74.cloudy.domain.util.DateUtils
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

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
            userId = userId,
            name = name,
            coverPath = null,
            nbPlayed = nbPlayed,
            isInQuickAccess = isInQuickAccess,
            artist = artist,
        )
}