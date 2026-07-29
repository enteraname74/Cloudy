package com.github.enteraname74.cloudy.metadata.model

import com.github.enteraname74.cloudy.domain.model.album.AlbumUpload
import com.github.enteraname74.cloudy.domain.model.artist.ArtistUpload
import com.github.enteraname74.cloudy.domain.model.music.MusicUpload

data class MusicMetadata(
    val name: String,
    val artists: List<Artist>,
    val album: Album,
    val duration: Long,
    val albumPosition: Int?,
) {
    data class Album(
        val name: String,
        val artist: Artist,
    ) {
        fun toAlbumUpload(): AlbumUpload =
            AlbumUpload(
                name = name,
                nbPlayed = 0,
                isInQuickAccess = false,
                artistUpload = artist.toArtistUpload(),
            )
    }

    data class Artist(
        val name: String,
    ) {
        fun toArtistUpload(): ArtistUpload =
            ArtistUpload(
                name = name,
                nbPlayed = 0,
                isInQuickAccess = false
            )
    }

    fun getMainArtistOrUnknown(): Artist =
        artists.firstOrNull() ?: Artist("Unknown")

    fun replaceBlank(): MusicMetadata =
        this.copy(
            name = name.ifBlank { "Unknown" },
            artists = artists.map {
                it.copy(
                    name = it.name.ifBlank { "Unknown" }
                )
            },
            album = album.copy(
                name = album.name.ifBlank { "Unknown" },
                artist = album.artist.copy(
                    name = album.artist.name.ifBlank { "Unknown" },
                )
            ),
        )

    fun toMusicUpload(): MusicUpload =
        MusicUpload(
            name = name,
            albumUpload = album.toAlbumUpload(),
            artists = artists.map { it.toArtistUpload() },
            albumPosition = albumPosition,
            duration = duration,
            nbPlayed = 0,
            isInQuickAccess = false,
        )

    companion object {

        fun unknownArtist(): Artist = Artist("Unknown")
        fun unknownMusicMetadata(): MusicMetadata =
            MusicMetadata(
                name = "Unknown",
                artists = listOf(
                    Artist(name = "Unknown")
                ),
                album = Album(
                    name = "Unknown",
                    artist = Artist(name = "Unknown")
                ),
                duration = 0L,
                albumPosition = null,
            )
    }
}
