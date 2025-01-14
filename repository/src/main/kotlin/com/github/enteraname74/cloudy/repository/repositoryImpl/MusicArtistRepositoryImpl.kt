package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.MusicArtist
import com.github.enteraname74.cloudy.domain.repository.MusicArtistRepository
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.repository.datasource.MusicArtistDataSource
import java.time.LocalDateTime
import java.util.UUID

class MusicArtistRepositoryImpl(
    private val musicArtistDataSource: MusicArtistDataSource,
) : MusicArtistRepository {
    override suspend fun upsert(musicArtist: MusicArtist) {
        musicArtistDataSource.upsert(
            musicArtist.copy(
                lastUpdateAt = LocalDateTime.now(),
            )
        )
    }

    override suspend fun delete(musicArtist: MusicArtist) {
        musicArtistDataSource.delete(musicArtist)
    }

    override suspend fun upsertAll(musicArtists: List<MusicArtist>) {
        musicArtistDataSource.upsertAll(musicArtists)
    }

    override suspend fun deleteAll(ids: List<String>) {
        musicArtistDataSource.deleteAll(ids)
    }

    override suspend fun isInMultipleArtist(musicId: UUID): Boolean =
        musicArtistDataSource.isInMultipleArtist(musicId)

    override suspend fun getAllOfUser(
        userId: UUID,
        paginatedRequest: PaginatedRequest
    ): List<MusicArtist> =
        musicArtistDataSource.getAllOfUser(
            userId = userId,
            paginatedRequest = paginatedRequest,
        )
}