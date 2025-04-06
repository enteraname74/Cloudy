package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.model.Artist
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.localdb.table.ArtistTable
import com.github.enteraname74.cloudy.localdb.table.MusicArtistTable
import com.github.enteraname74.cloudy.localdb.table.toArtist
import com.github.enteraname74.cloudy.localdb.util.suspendedTransaction
import com.github.enteraname74.cloudy.localdb.util.paginated
import com.github.enteraname74.cloudy.localdb.util.updatedAfter
import com.github.enteraname74.cloudy.repository.datasource.ArtistDataSource
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.upsert
import java.util.*

class ArtistDataSourceImpl : ArtistDataSource {
    override suspend fun getFromInformation(name: String, userId: UUID): Artist? =
        suspendedTransaction {
            ArtistTable
                .selectAll()
                .where { (ArtistTable.name eq name) and (ArtistTable.userId eq userId) }
                .firstOrNull()
                ?.toArtist()
        }

    override suspend fun getFromId(artistId: UUID): Artist? =
        suspendedTransaction {
            ArtistTable
                .selectAll()
                .where { ArtistTable.id eq artistId }
                .firstOrNull()
                ?.toArtist()
        }

    override suspend fun getFromCoverPath(coverPath: String): Artist? =
        suspendedTransaction {
            ArtistTable
                .selectAll()
                .where { ArtistTable.coverPath eq coverPath }
                .firstOrNull()
                ?.toArtist()
        }

    override suspend fun isArtistPossessedByUser(userId: UUID, artistId: UUID): Boolean =
        suspendedTransaction {
            ArtistTable
                .selectAll()
                .where { (ArtistTable.id eq artistId) and (ArtistTable.userId eq userId) }
                .count() > 0
        }

    override suspend fun upsert(artist: Artist): Artist =
        suspendedTransaction {
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
        suspendedTransaction {
            ArtistTable
                .selectAll()
                .where {
                    (ArtistTable.userId eq userId) and
                            (ArtistTable.lastUpdateAt updatedAfter paginatedRequest.lastUpdateAt)
                }
                .paginated(paginatedRequest)
                .mapNotNull { it.toArtist() }
        }

    override suspend fun deleteById(artistId: UUID): Boolean =
        suspendedTransaction {
            ArtistTable.deleteWhere {
                id eq artistId
            } > 0
        }

    override suspend fun deleteAll(artistIds: List<UUID>) {
        suspendedTransaction {
            ArtistTable.deleteWhere {
                id inList artistIds
            }
        }
    }

    override suspend fun getArtistsOfMusic(musicId: UUID): List<Artist> =
        suspendedTransaction {
            ArtistTable.join(
                otherTable = MusicArtistTable,
                joinType = JoinType.INNER,
                onColumn = ArtistTable.id,
                otherColumn = MusicArtistTable.artistId,
                additionalConstraint = {
                    MusicArtistTable.musicId eq musicId
                }
            )
                .selectAll()
                .mapNotNull { it.toArtist() }
        }
}