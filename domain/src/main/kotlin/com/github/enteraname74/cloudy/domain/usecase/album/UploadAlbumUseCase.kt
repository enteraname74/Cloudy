package com.github.enteraname74.cloudy.domain.usecase.album

import com.github.enteraname74.cloudy.domain.model.user.User
import com.github.enteraname74.cloudy.domain.model.album.Album
import com.github.enteraname74.cloudy.domain.model.album.AlbumUpload
import com.github.enteraname74.cloudy.domain.repository.AlbumRepository
import com.github.enteraname74.cloudy.domain.usecase.artist.UploadArtistUseCase

class UploadAlbumUseCase(
    private val uploadArtistUseCase: UploadArtistUseCase,
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(
        albumUpload: AlbumUpload,
        user: User,
    ): Album {
        val existingAlbum = albumRepository.getFromInformation(
            albumName = albumUpload.name,
            albumArtist = albumUpload.artistUpload.name,
            userId = user.id,
        )
        val artist = uploadArtistUseCase(
            artistUpload = albumUpload.artistUpload,
            user = user,
        )
        return if (existingAlbum != null) {
            albumRepository.upsert(
                album = existingAlbum.merge(
                    albumUpload = albumUpload,
                    artist = artist,
                ),
                coverData = null,
                username = user.username,
            )
        } else {
            albumRepository.upsert(
                album = albumUpload.toNewAlbum(
                    artist = artist,
                    userId = user.id,
                ),
                coverData = null,
                username = user.username,
            )
        }
    }
}