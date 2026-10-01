package com.github.enteraname74.cloudy.domain.usecase.music

import com.github.enteraname74.cloudy.domain.model.album.Album
import com.github.enteraname74.cloudy.domain.model.artist.Artist
import com.github.enteraname74.cloudy.domain.model.music.Music
import com.github.enteraname74.cloudy.domain.model.music.MusicUpdatePayload
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import com.github.enteraname74.cloudy.domain.usecase.DeleteEmptyAlbumsAndArtistsUseCase
import com.github.enteraname74.cloudy.domain.usecase.album.UpdateAlbumUseCase
import com.github.enteraname74.cloudy.domain.usecase.artist.SetArtistsOfMusicUseCase
import com.github.enteraname74.cloudy.domain.usecase.artist.UpdateArtistUseCase
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import kotlin.uuid.Uuid

class UpdateMusicUseCase(
    private val updateAlbumUseCase: UpdateAlbumUseCase,
    private val updateArtistUseCase: UpdateArtistUseCase,
    private val musicRepository: MusicRepository,
    private val setArtistsOfMusicUseCase: SetArtistsOfMusicUseCase,
    private val deleteEmptyAlbumsAndArtistsUseCase: DeleteEmptyAlbumsAndArtistsUseCase,
) {
    suspend operator fun invoke(
        payload: MusicUpdatePayload,
        userId: Uuid,
    ): CloudyResult<Music> {
        val existingMusic: Music = musicRepository.getFromUser(
            musicId = payload.spec.id,
            userId = userId,
        ) ?: return CloudyResult.Error()

        val artistOfMusic: List<Artist> = payload.spec.artists.map { artistUpdate ->
            updateArtistUseCase(
                artistUpdate = artistUpdate,
                userId = userId,
            )
        }
        val albumOfMusic: Album = updateAlbumUseCase(
            albumUpdate = payload.spec.album,
            userId = userId,
        )

        val result = musicRepository.upsert(
            music = existingMusic.merge(
                musicUpdateSpec = payload.spec,
                artists = artistOfMusic,
                album = albumOfMusic,
            ),
            cover = payload.musicCover,
        )

        return when (result) {
            is CloudyResult.Error -> result
            is CloudyResult.Success -> {
                setArtistsOfMusicUseCase(
                    musicId = result.data.id,
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