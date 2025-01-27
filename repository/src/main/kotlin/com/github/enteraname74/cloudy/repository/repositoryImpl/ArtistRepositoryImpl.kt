package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.Artist
import com.github.enteraname74.cloudy.domain.repository.ArtistRepository
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.repository.datasource.ArtistDataSource
import com.github.enteraname74.cloudy.repository.util.paginated
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class ArtistRepositoryImpl(
    private val artistDataSource: ArtistDataSource
): ArtistRepository {
    override suspend fun getFromInformation(name: String, userId: UUID): Artist? =
        artistDataSource.getFromInformation(
            name = name,
            userId = userId,
        )

    override suspend fun isArtistPossessedByUser(userId: UUID, artistId: UUID): Boolean =
        artistDataSource.isArtistPossessedByUser(
            userId = userId,
            artistId = artistId,
        )

    override suspend fun getFromId(artistId: UUID): Artist? =
        artistDataSource.getFromId(artistId = artistId)

    override suspend fun upsert(artist: Artist): Artist =
        artistDataSource.upsert(
            artist.copy(
                lastUpdateAt = LocalDateTime.now(ZoneOffset.UTC)
            )
        )

    override suspend fun getAllOfUser(
        userId: UUID,
        paginatedRequest: PaginatedRequest,
    ): List<Artist> =
        artistDataSource
            .getAllOfUser(
                userId = userId,
                paginatedRequest = paginatedRequest,
            )

    override suspend fun deleteById(artistId: UUID) =
        artistDataSource.deleteById(artistId)

    override suspend fun deleteAll(artistIds: List<UUID>) =
        artistDataSource.deleteAll(artistIds)

    override suspend fun getArtistsOfMusic(musicId: UUID): List<Artist> =
        artistDataSource.getArtistsOfMusic(musicId)
}