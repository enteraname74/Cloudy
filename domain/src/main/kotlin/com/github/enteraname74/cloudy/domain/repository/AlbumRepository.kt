package com.github.enteraname74.cloudy.domain.repository

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.album.Album
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import kotlin.uuid.Uuid

interface AlbumRepository {
    suspend fun getFromId(
        albumId: Uuid,
    ): Album?
    suspend fun getFromCoverPath(coverPath: String): Album?
    suspend fun getFromInformation(
        albumName: String,
        albumArtist: String,
        userId: Uuid
    ): Album?
    suspend fun getFromUser(
        albumId: Uuid,
        userId: Uuid,
    ): Album?
    suspend fun getAll(albumIds: List<Uuid>): List<Album>
    suspend fun upsert(
        album: Album,
        coverData: FileData?,
        username: String,
    ): Album
    suspend fun upsertAll(albums: List<Album>)
    suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest = PaginatedRequest(),
    ): List<Album>
    suspend fun deleteById(albumId: Uuid)
    suspend fun deleteAll(albumIds: List<Uuid>)
    suspend fun allOfArtist(artistId: Uuid): List<Album>
    suspend fun isAlbumPossessedByUser(userId: Uuid, albumId: Uuid): Boolean
    suspend fun deleteAllEmpty()
}