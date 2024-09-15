package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.Artist
import com.github.enteraname74.cloudy.domain.repository.ArtistRepository
import com.github.enteraname74.cloudy.repository.datasource.ArtistDataSource
import java.util.*

class ArtistRepositoryImpl(
    private val artistDataSource: ArtistDataSource
): ArtistRepository {
    override suspend fun getFromInformation(name: String, userId: UUID): Artist? =
        artistDataSource.getFromInformation(
            name = name,
            userId = userId,
        )

    override suspend fun upsert(artist: Artist): Artist =
        artistDataSource.upsert(artist)

    override suspend fun getAllOfUser(userId: UUID): List<Artist> =
        artistDataSource.getAllOfUser(userId)

    override suspend fun deleteById(artistId: UUID) {
        artistDataSource.deleteById(artistId)
    }
}