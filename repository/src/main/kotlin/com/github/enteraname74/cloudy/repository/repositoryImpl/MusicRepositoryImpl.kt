package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.Music
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.repository.datasource.MusicDataSource
import java.time.LocalDateTime
import java.util.*

class MusicRepositoryImpl(
    private val musicDataSource: MusicDataSource,
): MusicRepository {
    override suspend fun upsert(music: Music) {
        musicDataSource.upsert(music)
    }

    override suspend fun getFromInfo(musicId: UUID, userId: UUID): Music? =
        musicDataSource.getFromInfo(
            musicId = musicId,
            userId = userId,
        )

    override suspend fun deleteById(musicId: UUID) {
        musicDataSource.deleteById(musicId)
    }

    override suspend fun getAllOfUser(
        userId: UUID,
        paginatedRequest: PaginatedRequest,
    ): List<Music> =
        musicDataSource
            .getAllOfUser(userId = userId)
            .filter {
                val dateToCompare = paginatedRequest.lastUpdateAt ?: LocalDateTime.now()
                it.lastUpdatedAt.isAfter(dateToCompare) || it.lastUpdatedAt.isEqual(dateToCompare)
            }

    override suspend fun isMusicPossessedByUser(userId: UUID, musicId: UUID): Boolean =
        musicDataSource.isMusicPossessedByUser(userId, musicId)

    override suspend fun doesMusicExists(fingerprint: String, userId: UUID): Boolean =
        musicDataSource.doesMusicExists(
            fingerprint = fingerprint,
            userId = userId,
        )

    override suspend fun allFromAlbum(albumId: UUID, userId: UUID): List<Music> =
        musicDataSource.allFromAlbum(albumId, userId)

    override suspend fun allFromArtist(artistId: UUID, userId: UUID): List<Music> =
        musicDataSource.allFromArtist(artistId, userId)
}