package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.Album
import com.github.enteraname74.cloudy.domain.repository.AlbumRepository
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.repository.datasource.AlbumDataSource
import com.github.enteraname74.cloudy.repository.util.paginated
import java.time.LocalDateTime
import java.util.*

class AlbumRepositoryImpl(
    private val albumDataSource: AlbumDataSource
): AlbumRepository {
    override suspend fun getFromId(albumId: UUID): Album? =
        albumDataSource.getFromId(
            albumId = albumId,
        )

    override suspend fun getFromInformation(albumName: String, albumArtist: String, userId: UUID): Album? =
        albumDataSource.getFromInformation(
            albumName = albumName,
            albumArtist = albumArtist,
            userId = userId,
        )

    override suspend fun allOfArtist(artistId: UUID): List<Album> =
        albumDataSource.allOfArtist(
            artistId = artistId,
        )

    override suspend fun isAlbumPossessedByUser(userId: UUID, albumId: UUID): Boolean =
        albumDataSource.isAlbumPossessedByUser(
            userId = userId,
            albumId = albumId,
        )

    override suspend fun upsert(album: Album): Album =
        albumDataSource.upsert(
            album.copy(
                lastUpdateAt = LocalDateTime.now(),
            )
        )

    override suspend fun upsertAll(albums: List<Album>) {
        albumDataSource.upsertAll(
            albums.map {
                it.copy(
                    lastUpdateAt = LocalDateTime.now(),
                )
            }
        )
    }

    override suspend fun getAllOfUser(
        userId: UUID,
        paginatedRequest: PaginatedRequest,
    ): List<Album> =
        albumDataSource
            .getAllOfUser(userId)
            .paginated(paginatedRequest)

    override suspend fun deleteById(albumId: UUID) {
        albumDataSource.deleteById(albumId)
    }
}