package com.github.enteraname74.cloudy.domain.usecase.music

import com.github.enteraname74.cloudy.domain.model.Album
import com.github.enteraname74.cloudy.domain.model.Artist
import com.github.enteraname74.cloudy.domain.model.MusicUpload
import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import com.github.enteraname74.cloudy.domain.usecase.album.UploadAlbumUseCase
import com.github.enteraname74.cloudy.domain.usecase.artist.UploadArtistUseCase
import com.github.enteraname74.cloudy.domain.util.CloudyResult

class UploadMusicUseCase(
    private val uploadArtistUseCase: UploadArtistUseCase,
    private val uploadAlbumUseCase: UploadAlbumUseCase,
    private val musicRepository: MusicRepository,
) {
    suspend operator fun invoke(
        musicUpload: MusicUpload,
        fingerprint: String,
        user: User,
        musicPath: String,
    ): CloudyResult<Unit> {
        val artistOfMusic: List<Artist> = musicUpload.artists.map { artistUpload ->
            uploadArtistUseCase(
                artistUpload = artistUpload,
                user = user,
            )
        }
        val albumOfMusic: Album = uploadAlbumUseCase(
            albumUpload = musicUpload.albumUpload,
            user = user,
        )

        val exisingMusic = musicRepository.getFromFingerprint(
            fingerprint = fingerprint,
            userId = user.id,
        )
        return if (exisingMusic != null) {
            musicRepository.upsert(
                music = exisingMusic.merge(
                    musicUpload = musicUpload,
                    artists = artistOfMusic,
                    album = albumOfMusic,
                ),
                username = user.username,
                cover = null,
            )
        } else {
            musicRepository.upsert(
                music = musicUpload.toNewMusic(
                    userId = user.id,
                    artists = artistOfMusic,
                    album = albumOfMusic,
                    fingerprint = fingerprint,
                    path = musicPath,
                ),
                username = user.username,
                cover = null,
            )
        }.toSimple()
    }
}