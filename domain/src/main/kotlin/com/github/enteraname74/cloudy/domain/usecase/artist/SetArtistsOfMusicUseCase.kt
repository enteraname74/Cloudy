package com.github.enteraname74.cloudy.domain.usecase.artist

import com.github.enteraname74.cloudy.domain.model.MusicArtist
import com.github.enteraname74.cloudy.domain.model.music.MusicId
import com.github.enteraname74.cloudy.domain.repository.MusicArtistRepository
import kotlin.uuid.Uuid

/**
 * Sets the artists of a music: its own artists and the artist of its album.
 * The album artist is always linked, so that an album artist always has the musics of its albums
 * (and is never considered as an empty artist).
 */
class SetArtistsOfMusicUseCase(
    private val musicArtistRepository: MusicArtistRepository,
) {
    suspend operator fun invoke(
        musicId: MusicId,
        artistIds: List<Uuid>,
        albumArtistId: Uuid,
        userId: Uuid,
    ) {
        musicArtistRepository.deleteOfMusic(musicId = musicId)
        musicArtistRepository.upsertAll(
            musicArtists = (artistIds + albumArtistId).distinct().map {
                MusicArtist(
                    musicId = musicId,
                    artistId = it,
                    userId = userId,
                )
            }
        )
    }
}