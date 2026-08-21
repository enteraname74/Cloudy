package com.github.enteraname74.cloudy.domain.usecase.music

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.album.Album
import com.github.enteraname74.cloudy.domain.model.artist.Artist
import com.github.enteraname74.cloudy.domain.model.music.Music
import com.github.enteraname74.cloudy.domain.model.music.MusicUploadSpec
import com.github.enteraname74.cloudy.domain.model.user.User
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import com.github.enteraname74.cloudy.domain.usecase.DeleteEmptyAlbumsAndArtistsUseCase
import com.github.enteraname74.cloudy.domain.usecase.album.UploadAlbumUseCase
import com.github.enteraname74.cloudy.domain.usecase.artist.SetArtistsOfMusicUseCase
import com.github.enteraname74.cloudy.domain.usecase.artist.UploadArtistUseCase
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.domain.util.toCloudyResult
import com.github.enteraname74.cloudy.logging.CloudyLogger

class UploadMusicUseCase(
    private val uploadArtistUseCase: UploadArtistUseCase,
    private val uploadAlbumUseCase: UploadAlbumUseCase,
    private val musicRepository: MusicRepository,
    private val setArtistsOfMusicUseCase: SetArtistsOfMusicUseCase,
    private val deleteEmptyAlbumsAndArtistsUseCase: DeleteEmptyAlbumsAndArtistsUseCase,
) {
    private val logger = CloudyLogger(this::class)

    suspend operator fun invoke(
        musicUploadSpec: MusicUploadSpec,
        cover: FileData?,
        fingerprint: String,
        user: User,
        musicPath: String,
    ): CloudyResult<Music> {
        val artistOfMusic: List<Artist> = musicUploadSpec.artists.map { artistUpload ->
            uploadArtistUseCase(
                artistUpload = artistUpload,
                user = user,
            )
        }
        val albumOfMusic: Album = uploadAlbumUseCase(
            albumUpload = musicUploadSpec.albumUpload,
            user = user,
        )

        val existingMusic = musicRepository.getFromFingerprint(
            fingerprint = fingerprint,
            userId = user.id,
        )
        val result = if (existingMusic != null) {
            musicRepository.upsert(
                music = existingMusic.merge(
                    musicUploadSpec = musicUploadSpec,
                    artists = artistOfMusic,
                    album = albumOfMusic,
                ),
                username = user.username,
                cover = cover,
            )
        } else {
            musicRepository.upsert(
                music = musicUploadSpec.toNewMusic(
                    userId = user.id,
                    artists = artistOfMusic,
                    album = albumOfMusic,
                    fingerprint = fingerprint,
                    path = musicPath,
                ),
                username = user.username,
                cover = cover,
            )
        }

        return when (result) {
            is CloudyResult.Error -> {
                logger.error("Failed to persist music information (music name: ${musicUploadSpec.name})")
                result
            }
            is CloudyResult.Success -> {
                setArtistsOfMusicUseCase(
                    musicId = fingerprint,
                    artistIds = artistOfMusic.map { it.id },
                    userId = user.id,
                )
                // Clean up after saving updated data
                deleteEmptyAlbumsAndArtistsUseCase()
                musicRepository.getFromId(fingerprint).toCloudyResult()
            }
        }
    }
}