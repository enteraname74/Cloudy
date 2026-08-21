package com.github.enteraname74.cloudy.domain.model.music

import com.github.enteraname74.cloudy.domain.model.album.Album
import com.github.enteraname74.cloudy.domain.model.album.AlbumUpload
import com.github.enteraname74.cloudy.domain.model.artist.Artist
import com.github.enteraname74.cloudy.domain.model.artist.ArtistUpload
import com.github.enteraname74.cloudy.domain.util.DateUtils
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class MusicUploadSpec(
    val name: String,
    val albumUpload: AlbumUpload,
    val artists: List<ArtistUpload>,
    val albumPosition: Int?,
    val duration: Long,
    val nbPlayed: Int,
    val isInQuickAccess: Boolean,
) {
    fun toNewMusic(
        userId: Uuid,
        artists: List<Artist>,
        album: Album,
        fingerprint: String,
        path: String,
    ): Music =
        Music(
            fingerprint = fingerprint,
            userId = userId,
            name = name,
            album = album,
            artists = artists,
            path = path,
            albumPosition = albumPosition,
            duration = duration,
            addedDateMillis = DateUtils.now(),
            scope = Music.Scope.User,
        )
}