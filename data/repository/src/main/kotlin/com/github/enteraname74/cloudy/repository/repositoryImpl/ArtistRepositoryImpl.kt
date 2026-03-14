package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.Artist
import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.repository.ArtistRepository
import com.github.enteraname74.cloudy.domain.util.DateUtils
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.fileaccess.CoverFileManager
import com.github.enteraname74.cloudy.repository.datasource.ArtistDataSource
import kotlin.uuid.Uuid

class ArtistRepositoryImpl(
    private val artistDataSource: ArtistDataSource,
    private val coverFileManager: CoverFileManager,
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

    override suspend fun getFromCoverPath(coverPath: String): Artist? =
        artistDataSource.getFromCoverPath(coverPath = coverPath)

    override suspend fun upsert(
        artist: Artist,
        coverData: FileData?,
        username: String
    ): Artist {
        val savedId: Uuid? = coverData?.let { cover ->
            // We will delete the previous cover if any
            val previousName: String? =
                artist
                    .coverPath
                    ?.takeIf { it.startsWith(Artist.COVER_PATH) }
                    ?.split('/')?.last()

            previousName?.let { name ->
                coverFileManager.delete(
                    name = name,
                    username = username,
                )
            }

            coverFileManager.save(
                username = username,
                fileData = cover,
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

    override suspend fun deleteById(artistId: Uuid) =
        artistDataSource.deleteById(artistId)

    override suspend fun deleteAll(artistIds: List<Uuid>) =
        artistDataSource.deleteAll(artistIds)
}