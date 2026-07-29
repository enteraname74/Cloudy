package com.github.enteraname74.cloudy.domain.usecase.album

import com.github.enteraname74.cloudy.domain.model.user.User
import com.github.enteraname74.cloudy.domain.model.album.Album
import com.github.enteraname74.cloudy.domain.model.album.AlbumUpdate
import com.github.enteraname74.cloudy.domain.repository.AlbumRepository
import com.github.enteraname74.cloudy.domain.usecase.artist.UpdateArtistUseCase
import kotlin.uuid.Uuid

class UpdateAlbumUseCase(
    private val updateArtistUseCase: UpdateArtistUseCase,
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(
        albumUpdate: AlbumUpdate,
        user: User,
    ): Album {
        val existingAlbum = getExistingAlbum(
            albumUpdate = albumUpdate,
            userId = user.id,
        )

        return if (existingAlbum != null) {
            val artist = updateArtistUseCase(
                artistUpdate = albumUpdate.artist,
                user = user,
            )
            albumRepository.upsert(
                album = existingAlbum.merge(
                    albumUpdate = albumUpdate,
                    artist = artist,
                ),
                coverData = null,
                username = user.username,
            )
        } else {
            val newArtist = updateArtistUseCase(
                artistUpdate = albumUpdate.artist,
                user = user,
            )
            albumRepository.upsert(
                album = albumUpdate.toNewAlbum(
                    artist = newArtist,
                    userId = user.id,
                ),
                coverData = null,
                username = user.username,
            )
        }
    }

    private suspend fun getExistingAlbum(
        albumUpdate: AlbumUpdate,
        userId: Uuid,
    ): Album? =
        albumUpdate.id?.let {
            albumRepository.getFromUser(
                albumId = it,
                userId = userId,
            )
        } ?: albumRepository.getFromInformation(
            albumName = albumUpdate.name,
            albumArtist = albumUpdate.artist.name,
            userId = userId,
        )
}