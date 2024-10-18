package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.model.Artist
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.localdb.table.ArtistTable
import com.github.enteraname74.cloudy.localdb.table.toArtist
import com.github.enteraname74.cloudy.localdb.util.dbQuery
import com.github.enteraname74.cloudy.localdb.util.paginated
import com.github.enteraname74.cloudy.localdb.util.updatedAfter
import com.github.enteraname74.cloudy.repository.datasource.ArtistDataSource
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.upsert
import java.util.*

class ArtistDataSourceImpl : ArtistDataSource {
    override suspend fun getFromInformation(name: String, userId: UUID): Artist? =
        dbQuery {
            ArtistTable
                .selectAll()
                .where { (ArtistTable.name eq name) and (ArtistTable.userId eq userId) }
                .firstOrNull()
                ?.toArtist()
        }

    override suspend fun getFromId(artistId: UUID): Artist? =
        dbQuery {
            ArtistTable
                .selectAll()
                .where { ArtistTable.id eq artistId }
                .firstOrNull()
                ?.toArtist()
        }

    override suspend fun isArtistPossessedByUser(userId: UUID, artistId: UUID): Boolean =
        dbQuery {
            ArtistTable
                .selectAll()
                .where { (ArtistTable.id eq artistId) and (ArtistTable.userId eq userId) }
                .count() > 0
        }

    override suspend fun upsert(artist: Artist): Artist =
        dbQuery {
            ArtistTable.upsert {
                it[id] = artist.id
                it[userId] = artist.userId
                it[name] = artist.name
                it[coverPath] = artist.coverPath
                it[addedDate] = artist.addedDate
                it[nbPlayed] = artist.nbPlayed
                it[isInQuickAccess] = artist.isInQuickAccess
                it[lastUpdateAt] = artist.lastUpdateAt
            }

            ArtistTable
                .selectAll()
                .where { ArtistTable.id eq artist.id }
                .first()
                .toArtist()!!
        }

    override suspend fun getAllOfUser(
        userId: UUID,
        paginatedRequest: PaginatedRequest,
    ): List<Artist> =
        dbQuery {
            ArtistTable
                .selectAll()
                .where {
                    (ArtistTable.userId eq userId) and
                            (ArtistTable.lastUpdateAt updatedAfter paginatedRequest.lastUpdateAt)
                }
                .paginated(paginatedRequest)
                .mapNotNull { it.toArtist() }
        }

    override suspend fun deleteById(artistId: UUID) {
        dbQuery {
            ArtistTable.deleteWhere {
                ArtistTable.id eq artistId
            }
        }
    }
}