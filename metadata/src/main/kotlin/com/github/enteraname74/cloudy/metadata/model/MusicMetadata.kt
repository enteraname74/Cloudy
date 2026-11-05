package com.github.enteraname74.cloudy.metadata.model

import com.github.enteraname74.cloudy.domain.model.album.AlbumUpload
import com.github.enteraname74.cloudy.domain.model.artist.ArtistUpload
import com.github.enteraname74.cloudy.domain.model.music.MusicUploadSpec

data class MusicMetadata(
    val name: String,
    val artists: List<Artist>,
    val album: Album,
    val duration: Long,
    val albumPosition: Int?,
    val nbPlayed: Int,
    val isInQuickAccess: Boolean,
    // MusicBrainz id of the release group, only known when the metadata comes from Acoustid.
    val releaseGroupId: String? = null,
) {
    data class Album(
        val name: String,
        val artist: Artist,
        val nbPlayed: Int,
        val isInQuickAccess: Boolean,
    ) {
        fun toAlbumUpload(): AlbumUpload =
            AlbumUpload(
                name = name,
                nbPlayed = 0,
                isInQuickAccess = false,
                artistUpload = artist.toArtistUpload(),
            )

        companion object {
            fun fromAlbumUpload(albumUpload: AlbumUpload): Album =
                Album(
                    name = albumUpload.name,
                    artist = Artist.fromArtistUpload(albumUpload.artistUpload),
                    nbPlayed = albumUpload.nbPlayed,
                    isInQuickAccess = albumUpload.isInQuickAccess,
                )
        }
    }

    data class Artist(
        val name: String,
        val nbPlayed: Int,
        val isInQuickAccess: Boolean,
    ) {
        fun toArtistUpload(): ArtistUpload =
            ArtistUpload(
                name = name,
                nbPlayed = 0,
                isInQuickAccess = false
            )

        companion object {
            fun fromArtistUpload(artistUpload: ArtistUpload): Artist =
                Artist(
                    name = artistUpload.name,
                    nbPlayed = artistUpload.nbPlayed,
                    isInQuickAccess = artistUpload.isInQuickAccess,
                )
        }
    }

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

    fun toMusicUpload(): MusicUploadSpec =
        MusicUploadSpec(
            name = name,
            albumUpload = album.toAlbumUpload(),
            artists = artists.map { it.toArtistUpload() },
            albumPosition = albumPosition,
            duration = duration,
            nbPlayed = 0,
            isInQuickAccess = false,
        )

    fun updateNbPlayedAndQuickAccessFromOther(
        other: MusicMetadata
    ): MusicMetadata =
        copy(
            nbPlayed = other.nbPlayed,
            isInQuickAccess = other.isInQuickAccess,
            album = album.copy(
                nbPlayed = other.album.nbPlayed,
                isInQuickAccess = other.album.isInQuickAccess,
            ),
            artists = artists.map { artist ->
                val corresponding = other.artists.find { it.name == artist.name }
                if (corresponding != null) {
                    artist.copy(
                        nbPlayed = corresponding.nbPlayed,
                        isInQuickAccess = corresponding.isInQuickAccess,
                    )
                } else {
                    artist
                }
            }
        )

    companion object {

        fun unknownArtist(): Artist = Artist(
            name = "Unknown",
            nbPlayed = 0,
            isInQuickAccess = false,
        )

        fun unknownMusicMetadata(): MusicMetadata =
            MusicMetadata(
                name = "Unknown",
                artists = listOf(
                    Artist(
                        name = "Unknown",
                        nbPlayed = 0,
                        isInQuickAccess = false,
                    ),
                ),
                album = Album(
                    name = "Unknown",
                    artist = Artist(
                        name = "Unknown",
                        nbPlayed = 0,
                        isInQuickAccess = false,
                    ),
                    nbPlayed = 0,
                    isInQuickAccess = false,
                ),
                duration = 0L,
                albumPosition = null,
                nbPlayed = 0,
                isInQuickAccess = false,
            )

        fun fromUploadSpec(uploadSpec: MusicUploadSpec): MusicMetadata =
            MusicMetadata(
                name = uploadSpec.name,
                artists = uploadSpec.artists.map { Artist.fromArtistUpload(it) },
                album = Album.fromAlbumUpload(uploadSpec.albumUpload),
                duration = uploadSpec.duration,
                albumPosition = uploadSpec.albumPosition,
                nbPlayed = uploadSpec.nbPlayed,
                isInQuickAccess = uploadSpec.isInQuickAccess,
            )
    }
}
