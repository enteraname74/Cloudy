package com.github.enteraname74.cloudy.domain.repository

import com.github.enteraname74.cloudy.domain.model.Music
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import java.util.UUID

interface MusicRepository {
    suspend fun upsert(music: Music)
    suspend fun upsertAll(musics: List<Music>)
    suspend fun getFromId(musicId: UUID): Music?
    suspend fun deleteById(musicId: UUID)
    suspend fun getAllOfUser(
        userId: UUID,
        paginatedRequest: PaginatedRequest,
    ): List<Music>
    suspend fun isMusicPossessedByUser(userId: UUID, musicId: UUID): Boolean
    suspend fun doesMusicExists(fingerprint: String, userId: UUID): Boolean
    suspend fun allFromAlbum(albumId: UUID): List<Music>
    suspend fun allFromArtist(artistId: UUID): List<Music>
}