package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.model.Artist
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.localdb.table.ArtistEntity
import com.github.enteraname74.cloudy.localdb.table.ArtistTable
import com.github.enteraname74.cloudy.localdb.util.paginated
import com.github.enteraname74.cloudy.localdb.util.updatedAfter
import com.github.enteraname74.cloudy.localdb.util.workTransaction
import com.github.enteraname74.cloudy.repository.datasource.ArtistDataSource
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import kotlin.uuid.Uuid

class ArtistDataSourceImpl : ArtistDataSource {
    override suspend fun getFromInformation(name: String, userId: Uuid): Artist? =
        workTransaction {
            ArtistEntity
                .find { (ArtistTable.name eq name) and (ArtistTable.userId eq userId) }
                .firstOrNull()
                ?.toArtist()
        }

    override suspend fun getFromId(artistId: Uuid): Artist? =
        workTransaction {
            ArtistEntity
                .findById(artistId)
                ?.toArtist()
        }

    override suspend fun getFromCoverPath(coverPath: String): Artist? =
        workTransaction {
            ArtistEntity
                .find{ ArtistTable.coverPath eq coverPath }
                .firstOrNull()
                ?.toArtist()
        }

    override suspend fun isArtistPossessedByUser(userId: Uuid, artistId: Uuid): Boolean =
        workTransaction {
            ArtistEntity
                .find{ (ArtistTable.id eq artistId) and (ArtistTable.userId eq userId) }
                .count() > 0
        }

    override suspend fun upsert(artist: Artist): Artist =
        workTransaction {
            ArtistTable.upsertAll(listOf(artist))
            ArtistEntity.findById(artist.id)!!.toArtist()
        }

    override suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest,
    ): List<Artist> =
        workTransaction {
            ArtistEntity
                .find {
                    (ArtistTable.userId eq userId) and
                            (ArtistTable.lastUpdateAt updatedAfter paginatedRequest.lastUpdateAtMillis)
                }
                .paginated(paginatedRequest)
                .map { it.toArtist() }
        }

    override suspend fun deleteById(artistId: Uuid): Boolean =
        workTransaction {
            ArtistTable.deleteWhere {
                id eq artistId
            } > 0
        }

    override suspend fun deleteAll(artistIds: List<Uuid>) {
        workTransaction {
            ArtistTable.deleteWhere {
                id inList artistIds
            }
        }
    }
}