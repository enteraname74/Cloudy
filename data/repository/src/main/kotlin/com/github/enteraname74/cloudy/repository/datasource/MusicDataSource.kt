package com.github.enteraname74.cloudy.repository.datasource

import com.github.enteraname74.cloudy.domain.model.music.Music
import com.github.enteraname74.cloudy.domain.model.music.MusicId
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import kotlin.uuid.Uuid

interface MusicDataSource {
    suspend fun upsert(music: Music): Music
    suspend fun upsertAll(musics: List<Music>)
    suspend fun getFromUser(
        musicId: MusicId,
        userId: Uuid,
    ): Music?

    suspend fun getFromCoverPath(coverPath: String): Music?
    suspend fun getAll(ids: List<MusicId>): List<Music>
    suspend fun deleteAll(ids: List<MusicId>)
    suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest,
    ): List<Music>

    suspend fun getExistingIdsOfUser(
        userId: Uuid,
        ids: List<MusicId>,
    ): List<MusicId>

    suspend fun getExistingIds(
        ids: List<MusicId>,
    ): List<MusicId>

    suspend fun isMusicPossessedByUser(userId: Uuid, musicId: MusicId): Boolean
    suspend fun allFromAlbum(albumId: Uuid): List<Music>
    suspend fun allFromArtist(artistId: Uuid): List<Music>
}