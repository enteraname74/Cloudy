package com.github.enteraname74.cloudy.repository.datasource

import com.github.enteraname74.cloudy.domain.model.Album
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import kotlin.uuid.Uuid

interface AlbumDataSource {
    suspend fun getFromId(
        albumId: Uuid,
    ): Album?
    suspend fun getFromCoverPath(coverPath: String): Album?
    suspend fun getAll(albumIds: List<Uuid>): List<Album>
    suspend fun getFromInformation(
        albumName: String,
        albumArtist: String,
        userId: Uuid
    ): Album?
    suspend fun upsert(album: Album): Album
    suspend fun upsertAll(albums: List<Album>)
    suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest,
    ): List<Album>
    suspend fun deleteById(albumId: Uuid)
    suspend fun deleteAll(albumIds: List<Uuid>)
    suspend fun allOfArtist(artistId: Uuid): List<Album>
    suspend fun isAlbumPossessedByUser(userId: Uuid, albumId: Uuid): Boolean
    suspend fun deleteAllEmpty()
}