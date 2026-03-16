package com.github.enteraname74.cloudy.domain.model

import com.github.enteraname74.cloudy.domain.model.album.Album
import com.github.enteraname74.cloudy.domain.model.artist.Artist
import com.github.enteraname74.cloudy.domain.model.music.Music
import kotlinx.serialization.Serializable

@Serializable
data class UploadedMusicData(
    val music: Music,
    val album: Album,
    val artists: List<Artist>,
)
