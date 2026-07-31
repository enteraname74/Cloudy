package com.github.enteraname74.cloudy.domain.usecase.music

import com.github.enteraname74.cloudy.domain.model.album.Album
import com.github.enteraname74.cloudy.domain.model.artist.Artist
import com.github.enteraname74.cloudy.domain.model.music.Music
import com.github.enteraname74.cloudy.domain.model.music.MusicUpdate
import com.github.enteraname74.cloudy.domain.model.user.User
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import com.github.enteraname74.cloudy.domain.usecase.DeleteEmptyAlbumsAndArtistsUseCase
import com.github.enteraname74.cloudy.domain.usecase.album.UpdateAlbumUseCase
import com.github.enteraname74.cloudy.domain.usecase.artist.SetArtistsOfMusicUseCase
import com.github.enteraname74.cloudy.domain.usecase.artist.UpdateArtistUseCase
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.domain.util.toCloudyResult

class UpdateMusicUseCase(
    private val updateAlbumUseCase: UpdateAlbumUseCase,
    private val updateArtistUseCase: UpdateArtistUseCase,
    private val musicRepository: MusicRepository,
    private val setArtistsOfMusicUseCase: SetArtistsOfMusicUseCase,
    private val deleteEmptyAlbumsAndArtistsUseCase: DeleteEmptyAlbumsAndArtistsUseCase,
) {
    suspend operator fun invoke(
        musicUpdate: MusicUpdate,
        user: User,
    ): CloudyResult<Music> {
        val existingMusic: Music = musicRepository.getFromUser(
            musicId = musicUpdate.id,
            userId = user.id,
        ) ?: return CloudyResult.Error()

        val artistOfMusic: List<Artist> = musicUpdate.artists.map { artistUpdate ->
            updateArtistUseCase(
                artistUpdate = artistUpdate,
                user = user,
            )
        }
        val albumOfMusic: Album = updateAlbumUseCase(
            albumUpdate = musicUpdate.album,
            user = user,
        )

        val result = musicRepository.upsert(
            music = existingMusic.merge(
                musicUpdate = musicUpdate,
                artists = artistOfMusic,
                album = albumOfMusic,
            ),
            username = user.username,
            cover = null,
        )

        return when (result) {
            is CloudyResult.Error -> result
            is CloudyResult.Success -> {
                setArtistsOfMusicUseCase(
                    musicId = musicUpdate.id,
                    artistIds = artistOfMusic.map { it.id },
                    userId = user.id,
                )
                // Clean up after saving updated data
                deleteEmptyAlbumsAndArtistsUseCase()
                musicRepository.getFromId(musicUpdate.id).toCloudyResult()
            }
        }
    }
}