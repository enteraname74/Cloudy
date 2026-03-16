package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.album.Album
import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.repository.AlbumRepository
import com.github.enteraname74.cloudy.domain.util.DateUtils
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.fileaccess.CoverFileManager
import com.github.enteraname74.cloudy.repository.datasource.AlbumDataSource
import kotlin.uuid.Uuid

class AlbumRepositoryImpl(
    private val albumDataSource: AlbumDataSource,
    private val coverFileManager: CoverFileManager,
) : AlbumRepository {
    override suspend fun getFromId(albumId: Uuid): Album? =
        albumDataSource.getFromId(
            albumId = albumId,
        )

    override suspend fun getFromCoverPath(coverPath: String): Album? =
        albumDataSource.getFromCoverPath(coverPath)


    override suspend fun getAll(albumIds: List<Uuid>): List<Album> =
        albumDataSource.getAll(albumIds)

    override suspend fun getFromInformation(albumName: String, albumArtist: String, userId: Uuid): Album? =
        albumDataSource.getFromInformation(
            albumName = albumName,
            albumArtist = albumArtist,
            userId = userId,
        )

    override suspend fun getFromUser(
        albumId: Uuid,
        userId: Uuid
    ): Album? =
        albumDataSource.getFromUser(
            albumId = albumId,
            userId = userId,
        )

    override suspend fun allOfArtist(artistId: Uuid): List<Album> =
        albumDataSource.allOfArtist(
            artistId = artistId,
        )

    override suspend fun isAlbumPossessedByUser(userId: Uuid, albumId: Uuid): Boolean =
        albumDataSource.isAlbumPossessedByUser(
            userId = userId,
            albumId = albumId,
        )

    override suspend fun upsert(
        album: Album,
        coverData: FileData?,
        username: String,
    ): Album {
        val savedId: Uuid? = coverData?.let { cover ->
            // We will delete the previous cover if any
            val previousName: String? =
                album
                    .coverPath
                    ?.takeIf { it.startsWith(Album.COVER_PATH) }
                    ?.split('/')?.last()

            previousName?.let { name ->
                coverFileManager.delete(
                    name = name,
                    username = username,
                )
            }

            coverFileManager.save(
                username = username,
                fileData = cover,
            )
        }

        val newCoverPath = savedId?.let {
            "${Album.COVER_PATH}$it"
        }

        return albumDataSource.upsert(
            album.copy(
                lastUpdateAtMillis = DateUtils.now(),
                coverPath = newCoverPath ?: album.coverPath,
            )
        )
    }

    override suspend fun upsertAll(albums: List<Album>) {
        albumDataSource.upsertAll(
            albums.map {
                it.copy(
                    lastUpdateAtMillis = DateUtils.now(),
                )
            }
        )
    }

    override suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest,
    ): List<Album> =
        albumDataSource
            .getAllOfUser(
                userId = userId,
                paginatedRequest = paginatedRequest,
            )

    override suspend fun deleteById(albumId: Uuid) {
        albumDataSource.deleteById(albumId)
    }

    override suspend fun deleteAll(albumIds: List<Uuid>) {
        albumDataSource.deleteAll(albumIds)
    }

    override suspend fun deleteAllEmpty() {
        albumDataSource.deleteAllEmpty()
    }
}