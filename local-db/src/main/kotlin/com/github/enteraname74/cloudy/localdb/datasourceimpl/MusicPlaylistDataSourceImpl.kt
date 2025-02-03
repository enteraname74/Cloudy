package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.model.MusicPlaylist
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.localdb.table.MusicPlaylistTable
import com.github.enteraname74.cloudy.localdb.table.toMusicPlaylist
import com.github.enteraname74.cloudy.localdb.util.paginated
import com.github.enteraname74.cloudy.localdb.util.suspendedTransaction
import com.github.enteraname74.cloudy.localdb.util.updatedAfter
import com.github.enteraname74.cloudy.repository.datasource.MusicPlaylistDataSource
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import java.util.*

class MusicPlaylistDataSourceImpl : MusicPlaylistDataSource {
    override suspend fun upsert(musicPlaylist: MusicPlaylist) {
        suspendedTransaction {
            MusicPlaylistTable.upsert {
                it[id] = musicPlaylist.id
                it[musicId] = musicPlaylist.musicId
                it[playlistId] = musicPlaylist.playlistId
                it[userId] = musicPlaylist.userId
                it[lastUpdateAt] = musicPlaylist.lastUpdateAt
            }
        }
    }

    override suspend fun upsertAll(musicPlaylists: List<MusicPlaylist>) {
        suspendedTransaction {
            MusicPlaylistTable.batchUpsert(musicPlaylists) { musicPlaylist ->
                this[MusicPlaylistTable.id] = musicPlaylist.id
                this[MusicPlaylistTable.musicId] = musicPlaylist.musicId
                this[MusicPlaylistTable.playlistId] = musicPlaylist.playlistId
                this[MusicPlaylistTable.userId] = musicPlaylist.userId
                this[MusicPlaylistTable.lastUpdateAt] = musicPlaylist.lastUpdateAt
            }
        }
    }

    override suspend fun delete(musicPlaylist: MusicPlaylist) {
        suspendedTransaction {
            MusicPlaylistTable.deleteWhere {
                id eq musicPlaylist.id
            }
        }
    }

    override suspend fun deleteAll(ids: List<String>) {
        suspendedTransaction {
            MusicPlaylistTable.deleteWhere {
                id inList ids
            }
        }
    }

    override suspend fun getAllOfPlaylist(playlistId: UUID): List<MusicPlaylist> =
        suspendedTransaction {
            MusicPlaylistTable
                .selectAll()
                .where { MusicPlaylistTable.playlistId eq playlistId }
                .mapNotNull { it.toMusicPlaylist() }
        }

    override suspend fun getAllOfUser(
        userId: UUID,
        paginatedRequest: PaginatedRequest
    ): List<MusicPlaylist> =
        suspendedTransaction {
            MusicPlaylistTable
                .selectAll()
                .where {
                    (MusicPlaylistTable.userId eq userId) and
                            (MusicPlaylistTable.lastUpdateAt updatedAfter paginatedRequest.lastUpdateAt)
                }
                .paginated(paginatedRequest)
                .mapNotNull { it.toMusicPlaylist() }
        }
}