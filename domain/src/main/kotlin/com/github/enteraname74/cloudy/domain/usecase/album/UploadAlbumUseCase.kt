package com.github.enteraname74.cloudy.domain.usecase.album

import com.github.enteraname74.cloudy.domain.model.album.Album
import com.github.enteraname74.cloudy.domain.model.album.AlbumUpload
import com.github.enteraname74.cloudy.domain.repository.AlbumRepository
import com.github.enteraname74.cloudy.domain.usecase.artist.UploadArtistUseCase
import kotlin.uuid.Uuid

class UploadAlbumUseCase(
    private val uploadArtistUseCase: UploadArtistUseCase,
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(
        albumUpload: AlbumUpload,
        userId: Uuid,
    ): Album {
        val existingAlbum = albumRepository.getFromInformation(
            albumName = albumUpload.name,
            albumArtist = albumUpload.artistUpload.name,
            userId = userId,
        )
        val artist = uploadArtistUseCase(
            artistUpload = albumUpload.artistUpload,
            userId = userId,
        )
        return if (existingAlbum != null) {
            albumRepository.upsert(
                album = existingAlbum.merge(
                    albumUpload = albumUpload,
                    artist = artist,
                ),
                coverData = null,
            )
        } else {
            albumRepository.upsert(
                album = albumUpload.toNewAlbum(
                    artist = artist,
                    userId = userId,
                ),
                coverData = null,
            )
        }
    }
}