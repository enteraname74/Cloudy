package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.ext.toUUID
import com.github.enteraname74.cloudy.domain.model.Album
import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.repository.AlbumRepository
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.fileaccess.CoverFileManager
import com.github.enteraname74.cloudy.repository.datasource.AlbumDataSource
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class AlbumRepositoryImpl(
    private val albumDataSource: AlbumDataSource,
    private val coverFileManager: CoverFileManager,
) : AlbumRepository {
    override suspend fun getFromId(albumId: UUID): Album? =
        albumDataSource.getFromId(
            albumId = albumId,
        )

    override suspend fun getFromCoverPath(coverPath: String): Album? =
        albumDataSource.getFromCoverPath(coverPath)


    override suspend fun getAll(albumIds: List<UUID>): List<Album> =
        albumDataSource.getAll(albumIds)

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

    override suspend fun upsert(
        album: Album,
        coverData: FileData?,
        username: String,
    ): Album {
        val savedId: UUID? = coverData?.let {
            // We will delete the previous cover if any
            val previousId: UUID? =
                album
                    .coverPath
                    ?.takeIf { it.startsWith(Album.COVER_PATH) }
                    ?.split('/')?.last()?.toUUID()

            previousId?.let { id ->
                coverFileManager.delete(
                    id = id,
                    username = username,
                )
            }

            coverFileManager.save(
                username = username,
                fileData = it,
            )
        }

        val newCoverPath = savedId?.let {
            "${Album.COVER_PATH}$it"
        }

        return albumDataSource.upsert(
            album.copy(
                lastUpdateAt = LocalDateTime.now(ZoneOffset.UTC),
                coverPath = newCoverPath ?: album.coverPath,
            )
        )
    }

    override suspend fun upsertAll(albums: List<Album>) {
        albumDataSource.upsertAll(
            albums.map {
                it.copy(
                    lastUpdateAt = LocalDateTime.now(ZoneOffset.UTC),
                )
            }
        )
    }

    override suspend fun getAllOfUser(
        userId: UUID,
        paginatedRequest: PaginatedRequest,
    ): List<Album> =
        albumDataSource
            .getAllOfUser(
                userId = userId,
                paginatedRequest = paginatedRequest,
            )

    override suspend fun deleteById(albumId: UUID) {
        albumDataSource.deleteById(albumId)
    }

    override suspend fun deleteAll(albumIds: List<UUID>) {
        albumDataSource.deleteAll(albumIds)
    }
}