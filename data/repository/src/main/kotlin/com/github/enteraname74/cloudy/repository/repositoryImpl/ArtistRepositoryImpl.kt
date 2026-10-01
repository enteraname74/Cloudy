package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.artist.Artist
import com.github.enteraname74.cloudy.domain.repository.ArtistRepository
import com.github.enteraname74.cloudy.domain.util.DateUtils
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.repository.datasource.ArtistDataSource
import com.github.enteraname74.cloudy.repository.datasource.CoverDataSource
import kotlin.uuid.Uuid

class ArtistRepositoryImpl(
    private val artistDataSource: ArtistDataSource,
    private val coverDataSource: CoverDataSource,
) : ArtistRepository {
    override suspend fun getFromInformation(name: String, userId: Uuid): Artist? =
        artistDataSource.getFromInformation(
            name = name,
            userId = userId,
        )

    override suspend fun isArtistPossessedByUser(userId: Uuid, artistId: Uuid): Boolean =
        artistDataSource.isArtistPossessedByUser(
            userId = userId,
            artistId = artistId,
        )

    override suspend fun getFromId(artistId: Uuid): Artist? =
        artistDataSource.getFromId(artistId = artistId)

    override suspend fun getFromUser(artistId: Uuid, userId: Uuid): Artist? =
        artistDataSource.getFromUser(
            artistId = artistId,
            userId = userId,
        )

    override suspend fun getFromCoverPath(coverPath: String): Artist? =
        artistDataSource.getFromCoverPath(coverPath = coverPath)

    override suspend fun upsert(
        artist: Artist,
        coverData: FileData?,
    ): Artist {
        val savedId: Uuid? = coverData?.let { cover ->
            // We will delete the previous cover if any
            val previousName: String? =
                artist
                    .coverPath
                    ?.takeIf { it.startsWith(Artist.COVER_PATH) }
                    ?.split('/')?.last()

            previousName?.let { name ->
                coverDataSource.delete(
                    name = name,
                    userId = artist.userId,
                )
            }

            coverDataSource.save(
                userId = artist.userId,
                data = cover,
            )
        }

        val newCoverPath = savedId?.let {
            "${Artist.COVER_PATH}$it"
        }

        return artistDataSource.upsert(
            artist.copy(
                lastUpdateAtMillis = DateUtils.now(),
                coverPath = newCoverPath ?: artist.coverPath,
            )
        )
    }

    override suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest,
    ): List<Artist> =
        artistDataSource
            .getAllOfUser(
                userId = userId,
                paginatedRequest = paginatedRequest,
            )

    override suspend fun deleteAll(artistIds: List<Uuid>) {
        artistDataSource.deleteAll(artistIds)
    }

    override suspend fun deleteOfUser(userId: Uuid) {
        artistDataSource.deleteOfUser(userId = userId)
    }

    override suspend fun deleteAllEmpty() {
        artistDataSource.deleteAllEmpty()
    }
}