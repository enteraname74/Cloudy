package com.github.enteraname74.cloudy.domain.model.music

import com.github.enteraname74.cloudy.domain.model.album.AlbumUpdate
import com.github.enteraname74.cloudy.domain.model.artist.ArtistUpdate
import kotlinx.serialization.Serializable

@Serializable
data class MusicUpdate(
    val id: String,
    val name: String,
    val album: AlbumUpdate,
    val artists: List<ArtistUpdate>,
    val albumPosition: Int?,
    val nbPlayed: Int,
    val isInQuickAccess: Boolean,
) {
    fun isValid(): Boolean =
        name.isNotBlank()
                && nbPlayed >= 0
                && (albumPosition?.let { it >= 0 } ?: true)
                && album.isValid()
                && artists.all { it.isValid() }
}
