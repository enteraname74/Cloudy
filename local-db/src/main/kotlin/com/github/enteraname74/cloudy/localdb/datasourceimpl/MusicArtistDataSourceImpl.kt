package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.model.MusicArtist
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.localdb.table.MusicArtistTable
import com.github.enteraname74.cloudy.localdb.table.MusicArtistTable.artistId
import com.github.enteraname74.cloudy.localdb.table.MusicArtistTable.id
import com.github.enteraname74.cloudy.localdb.table.MusicArtistTable.lastUpdateAt
import com.github.enteraname74.cloudy.localdb.table.MusicArtistTable.musicId
import com.github.enteraname74.cloudy.localdb.table.MusicArtistTable.userId
import com.github.enteraname74.cloudy.localdb.table.toMusicArtist
import com.github.enteraname74.cloudy.localdb.util.paginated
import com.github.enteraname74.cloudy.localdb.util.suspendedTransaction
import com.github.enteraname74.cloudy.localdb.util.updatedAfter
import com.github.enteraname74.cloudy.repository.datasource.MusicArtistDataSource
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.upsert
import java.util.*

class MusicArtistDataSourceImpl: MusicArtistDataSource {
    override suspend fun upsert(musicArtist: MusicArtist) {
        suspendedTransaction {
            MusicArtistTable.upsert {
                it[id] = musicArtist.id
                it[musicId] = musicArtist.musicId
                it[artistId] = musicArtist.artistId
                it[userId] = musicArtist.userId
                it[lastUpdateAt] = musicArtist.lastUpdateAt
            }
        }
    }

    override suspend fun delete(musicArtist: MusicArtist) {
        suspendedTransaction {
            MusicArtistTable.deleteWhere {
                id eq musicArtist.id
            }
        }
    }

    override suspend fun upsertAll(musicArtists: List<MusicArtist>) {
        suspendedTransaction {
            MusicArtistTable.batchUpsert(musicArtists){ musicArtist ->
                this[id] = musicArtist.id
                this[musicId] = musicArtist.musicId
                this[artistId] = musicArtist.artistId
                this[userId] = musicArtist.userId
                this[lastUpdateAt] = musicArtist.lastUpdateAt
            }
        }
    }

    override suspend fun deleteAll(ids: List<String>) {
        suspendedTransaction {
            MusicArtistTable.deleteWhere {
                id inList ids
            }
        }
    }

    override suspend fun isInMultipleArtist(musicId: UUID): Boolean =
        suspendedTransaction {
            MusicArtistTable
                .selectAll()
                .where { MusicArtistTable.musicId eq musicId }
                .count() > 1
        }

    override suspend fun getAllOfUser(
        userId: UUID,
        paginatedRequest: PaginatedRequest
    ): List<MusicArtist> =
        suspendedTransaction {
            MusicArtistTable
                .selectAll()
                .where {
                    (MusicArtistTable.userId eq userId) and
                            (lastUpdateAt updatedAfter paginatedRequest.lastUpdateAt)
                }
                .paginated(paginatedRequest)
                .mapNotNull { it.toMusicArtist() }
        }

}