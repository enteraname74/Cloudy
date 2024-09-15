package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.Album
import com.github.enteraname74.cloudy.domain.repository.AlbumRepository
import com.github.enteraname74.cloudy.repository.datasource.AlbumDataSource
import java.util.*

class AlbumRepositoryImpl(
    private val albumDataSource: AlbumDataSource
): AlbumRepository {
    override suspend fun getFromInformation(albumName: String, albumArtist: String, userId: UUID): Album? =
        albumDataSource.getFromInformation(
            albumName = albumName,
            albumArtist = albumArtist,
            userId = userId,
        )

    override suspend fun upsert(album: Album): Album =
        albumDataSource.upsert(album)

    override suspend fun getAllOfUser(userId: UUID): List<Album> =
        albumDataSource.getAllOfUser(userId)

    override suspend fun deleteById(albumId: UUID) {
        albumDataSource.deleteById(albumId)
    }
}