package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.MusicArtist
import com.github.enteraname74.cloudy.domain.repository.MusicArtistRepository
import com.github.enteraname74.cloudy.domain.util.DateUtils
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.repository.datasource.MusicArtistDataSource
import kotlin.uuid.Uuid

class MusicArtistRepositoryImpl(
    private val musicArtistDataSource: MusicArtistDataSource,
) : MusicArtistRepository {
    override suspend fun upsert(musicArtist: MusicArtist) {
        musicArtistDataSource.upsert(
            musicArtist.copy(
                lastUpdateAtMillis = DateUtils.now(),
            )
        )
    }

    override suspend fun delete(musicArtist: MusicArtist) {
        musicArtistDataSource.delete(musicArtist)
    }

    override suspend fun upsertAll(musicArtists: List<MusicArtist>) {
        musicArtistDataSource.upsertAll(
            musicArtists = musicArtists.map {
                it.copy(
                    lastUpdateAtMillis = DateUtils.now(),
                )
            }
        )
    }

    override suspend fun deleteAll(ids: List<String>) {
        musicArtistDataSource.deleteAll(ids)
    }

    override suspend fun deleteOfMusic(musicId: String) {
        musicArtistDataSource.deleteOfMusic(musicId)
    }

    override suspend fun isInMultipleArtist(musicId: String): Boolean =
        musicArtistDataSource.isInMultipleArtist(musicId)

    override suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest
    ): List<MusicArtist> =
        musicArtistDataSource.getAllOfUser(
            userId = userId,
            paginatedRequest = paginatedRequest,
        )
}