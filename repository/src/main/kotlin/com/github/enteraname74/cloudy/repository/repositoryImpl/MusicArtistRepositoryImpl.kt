package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.MusicArtist
import com.github.enteraname74.cloudy.domain.repository.MusicArtistRepository
import com.github.enteraname74.cloudy.repository.datasource.MusicArtistDataSource

class MusicArtistRepositoryImpl(
    private val musicArtistDataSource: MusicArtistDataSource,
): MusicArtistRepository {
    override suspend fun upsert(musicArtist: MusicArtist) {
        musicArtistDataSource.upsert(musicArtist)
    }

    override suspend fun delete(musicArtist: MusicArtist) {
        musicArtistDataSource.delete(musicArtist)
    }
}