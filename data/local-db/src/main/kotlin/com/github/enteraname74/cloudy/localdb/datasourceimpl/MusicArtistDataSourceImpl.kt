package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.model.MusicArtist
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.localdb.table.MusicArtistEntity
import com.github.enteraname74.cloudy.localdb.table.MusicArtistTable
import com.github.enteraname74.cloudy.localdb.table.MusicArtistTable.lastUpdateAt
import com.github.enteraname74.cloudy.localdb.util.paginated
import com.github.enteraname74.cloudy.localdb.util.updatedAfter
import com.github.enteraname74.cloudy.localdb.util.workTransaction
import com.github.enteraname74.cloudy.repository.datasource.MusicArtistDataSource
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.upsert
import kotlin.uuid.Uuid

class MusicArtistDataSourceImpl : MusicArtistDataSource {
    override suspend fun upsert(musicArtist: MusicArtist) {
        workTransaction {
            MusicArtistTable.upsert {
                it[id] = musicArtist.id
                it[musicId] = musicArtist.musicId
                it[artistId] = musicArtist.artistId
                it[userId] = musicArtist.userId
                it[lastUpdateAt] = musicArtist.lastUpdateAtMillis
            }
        }
    }

    override suspend fun delete(musicArtist: MusicArtist) {
        workTransaction {
            MusicArtistEntity.findById(musicArtist.id)?.delete()
        }
    }

    override suspend fun upsertAll(musicArtists: List<MusicArtist>) {
        workTransaction {
            MusicArtistTable.upsertAll(musicArtists)
        }
    }

    override suspend fun deleteAll(ids: List<String>) {
        workTransaction {
            MusicArtistTable.deleteWhere {
                id inList ids
            }
        }
    }

    override suspend fun deleteOfMusic(musicId: String) {
        workTransaction {
            MusicArtistTable.deleteWhere {
                this.musicId eq musicId
            }
        }
    }

    override suspend fun isInMultipleArtist(musicId: String): Boolean =
        workTransaction {
            MusicArtistEntity
                .find { MusicArtistTable.musicId eq musicId }
                .count() > 1
        }

    override suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest
    ): List<MusicArtist> =
        workTransaction {
            MusicArtistEntity
                .find {
                    (MusicArtistTable.userId eq userId) and
                            (lastUpdateAt updatedAfter paginatedRequest.lastUpdateAtMillis)
                }
                .paginated(paginatedRequest)
                .map { it.toMusicArtist() }
        }

}