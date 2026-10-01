package com.github.enteraname74.cloudy.domain.model.album

import com.github.enteraname74.cloudy.domain.model.artist.Artist
import com.github.enteraname74.cloudy.domain.model.artist.ArtistUpdate
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class AlbumUpdate(
    val id: Uuid? = null,
    val name: String,
    val nbPlayed: Int,
    val isInQuickAccess: Boolean,
    val artist: ArtistUpdate,
) {
    fun isValid(): Boolean =
        name.isNotBlank() && nbPlayed >= 0 && artist.isValid()

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
