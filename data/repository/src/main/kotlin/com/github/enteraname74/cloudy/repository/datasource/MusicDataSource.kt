package com.github.enteraname74.cloudy.repository.datasource

import com.github.enteraname74.cloudy.domain.model.Music
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import kotlin.uuid.Uuid

interface MusicDataSource {
    suspend fun upsert(music: Music): Music
    suspend fun upsertAll(musics: List<Music>)
    suspend fun getFromId(musicId: String): Music?
    suspend fun getFromCoverPath(coverPath: String): Music?
    suspend fun getAll(ids: List<String>): List<Music>
    suspend fun deleteAll(ids: List<String>)
    suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest,
    ): List<Music>
    suspend fun isMusicPossessedByUser(userId: Uuid, musicId: String): Boolean
    suspend fun getFromFingerprint(fingerprint: String, userId: Uuid): Music?
    suspend fun allFromAlbum(albumId: Uuid): List<Music>
    suspend fun allFromArtist(artistId: Uuid): List<Music>
}