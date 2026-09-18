package com.github.enteraname74.cloudy.domain.usecase.music

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.album.Album
import com.github.enteraname74.cloudy.domain.model.artist.Artist
import com.github.enteraname74.cloudy.domain.model.music.Music
import com.github.enteraname74.cloudy.domain.model.music.MusicId
import com.github.enteraname74.cloudy.domain.model.music.MusicUploadSpec
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import com.github.enteraname74.cloudy.domain.usecase.DeleteEmptyAlbumsAndArtistsUseCase
import com.github.enteraname74.cloudy.domain.usecase.album.UploadAlbumUseCase
import com.github.enteraname74.cloudy.domain.usecase.artist.SetArtistsOfMusicUseCase
import com.github.enteraname74.cloudy.domain.usecase.artist.UploadArtistUseCase
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.logging.CloudyLogger
import kotlin.uuid.Uuid

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
        userId: Uuid,
        musicPath: String,
    ): CloudyResult<Music> {
        val artistOfMusic: List<Artist> = musicUploadSpec.artists.map { artistUpload ->
            uploadArtistUseCase(
                artistUpload = artistUpload,
                userId = userId,
            )
        }
        val albumOfMusic: Album = uploadAlbumUseCase(
            albumUpload = musicUploadSpec.albumUpload,
            userId = userId,
        )

        val existingMusic = musicRepository.getFromUser(
            musicId = MusicId(
                fingerprint = fingerprint,
                userId = userId,
            ),
            userId = userId,
        )
        val result = if (existingMusic != null) {
            musicRepository.upsert(
                music = existingMusic.merge(
                    musicUploadSpec = musicUploadSpec,
                    artists = artistOfMusic,
                    album = albumOfMusic,
                ),
                cover = cover,
            )
        } else {
            musicRepository.upsert(
                music = musicUploadSpec.toNewMusic(
                    userId = userId,
                    artists = artistOfMusic,
                    album = albumOfMusic,
                    fingerprint = fingerprint,
                    path = musicPath,
                ),
                cover = cover,
            )
        }

        return when (result) {
            is CloudyResult.Error -> {
                logger.error("Failed to persist music information (music name: ${musicUploadSpec.name})")
                result
            }
            is CloudyResult.Success -> {
                val music = result.data
                setArtistsOfMusicUseCase(
                    musicId = music.id,
                    artistIds = artistOfMusic.map { it.id },
                    userId = userId,
                )
                // Clean up after saving updated data
                deleteEmptyAlbumsAndArtistsUseCase(userId)
                result
            }
        }
    }
}