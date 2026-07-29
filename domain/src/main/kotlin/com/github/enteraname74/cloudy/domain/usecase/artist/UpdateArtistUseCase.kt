package com.github.enteraname74.cloudy.domain.usecase.artist

import com.github.enteraname74.cloudy.domain.model.user.User
import com.github.enteraname74.cloudy.domain.model.artist.Artist
import com.github.enteraname74.cloudy.domain.model.artist.ArtistUpdate
import com.github.enteraname74.cloudy.domain.repository.ArtistRepository
import kotlin.uuid.Uuid

class UpdateArtistUseCase(
    private val artistRepository: ArtistRepository,
) {
    suspend operator fun invoke(
        artistUpdate: ArtistUpdate,
        user: User,
    ): Artist {
        val existingArtist: Artist? = getExistingArtist(
            artistUpdate = artistUpdate,
            userId = user.id,
        )

        return if (existingArtist == null) {
            artistRepository.upsert(
                artist = artistUpdate.toNewArtist(user.id),
                coverData = null,
                username = user.username,
            )
        } else {
            artistRepository.upsert(
                artist = existingArtist.merge(artistUpdate),
                coverData = null,
                username = user.username,
            )
        }
    }

    private suspend fun getExistingArtist(
        artistUpdate: ArtistUpdate,
        userId: Uuid,
    ): Artist? =
        artistUpdate.id?.let {
            artistRepository.getFromUser(
                artistId = it,
                userId = userId,
            )
        } ?: artistRepository.getFromInformation(
            name = artistUpdate.name,
            userId = userId,
        )
}