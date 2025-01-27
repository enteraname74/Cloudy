package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.model.Playlist
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.localdb.table.PlaylistTable
import com.github.enteraname74.cloudy.localdb.table.PlaylistTable.addedDate
import com.github.enteraname74.cloudy.localdb.table.PlaylistTable.coverPath
import com.github.enteraname74.cloudy.localdb.table.PlaylistTable.isFavorite
import com.github.enteraname74.cloudy.localdb.table.PlaylistTable.isInQuickAccess
import com.github.enteraname74.cloudy.localdb.table.PlaylistTable.lastUpdateAt
import com.github.enteraname74.cloudy.localdb.table.PlaylistTable.name
import com.github.enteraname74.cloudy.localdb.table.PlaylistTable.nbPlayed
import com.github.enteraname74.cloudy.localdb.table.PlaylistTable.userId
import com.github.enteraname74.cloudy.localdb.table.toPlaylist
import com.github.enteraname74.cloudy.localdb.util.paginated
import com.github.enteraname74.cloudy.localdb.util.suspendedTransaction
import com.github.enteraname74.cloudy.localdb.util.updatedAfter
import com.github.enteraname74.cloudy.repository.datasource.PlaylistDataSource
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import java.util.*

class PlaylistDataSourceImpl: PlaylistDataSource {
    override suspend fun getFromId(playlistId: UUID): Playlist? =
        suspendedTransaction {
            PlaylistTable
                .selectAll()
                .where {
                    PlaylistTable.id eq playlistId
                }.firstOrNull()
                ?.toPlaylist()
        }

    override suspend fun getFromInformation(name: String, userId: UUID): Playlist? =
        suspendedTransaction {
            PlaylistTable
                .selectAll()
                .where {
                    (PlaylistTable.name eq name) and (PlaylistTable.userId eq userId)
                }.firstOrNull()
                ?.toPlaylist()
        }

    override suspend fun upsert(playlist: Playlist): Playlist =
        suspendedTransaction {
            PlaylistTable.upsert {
                it[id] = playlist.id
                it[userId] = playlist.userId
                it[name] = playlist.name
                it[coverPath] = playlist.coverPath
                it[isFavorite] = playlist.isFavorite
                it[addedDate] = playlist.addedDate
                it[nbPlayed] = playlist.nbPlayed
                it[isInQuickAccess] = playlist.isInQuickAccess
                it[lastUpdateAt] = playlist.lastUpdateAt
            }

            PlaylistTable
                .selectAll()
                .where { PlaylistTable.id eq playlist.id }
                .first()
                .toPlaylist()!!
        }

    override suspend fun upsertAll(playlists: List<Playlist>): List<Playlist> =
        suspendedTransaction {
            PlaylistTable.batchUpsert(playlists) { playlist ->
                this[PlaylistTable.id] = playlist.id
                this[userId] = playlist.userId
                this[name] = playlist.name
                this[coverPath] = playlist.coverPath
                this[isFavorite] = playlist.isFavorite
                this[addedDate] = playlist.addedDate
                this[nbPlayed] = playlist.nbPlayed
                this[isInQuickAccess] = playlist.isInQuickAccess
                this[lastUpdateAt] = playlist.lastUpdateAt
            }

            val playlistIds = playlists.map { it.id }

            PlaylistTable
                .selectAll()
                .where { PlaylistTable.id inList playlistIds }
                .mapNotNull { it.toPlaylist() }
        }

    override suspend fun deleteById(playlistId: UUID): Boolean =
        suspendedTransaction {
            PlaylistTable.deleteWhere {
                id eq playlistId
            } > 0
        }

    override suspend fun deleteAll(playlistIds: List<UUID>) {
        suspendedTransaction {
            PlaylistTable.deleteWhere {
                id inList playlistIds
            }
        }
    }

    override suspend fun allOfUser(userId: UUID, paginatedRequest: PaginatedRequest): List<Playlist> =
        suspendedTransaction {
            PlaylistTable
                .selectAll()
                .where {
                    (PlaylistTable.userId eq userId) and
                            (lastUpdateAt updatedAfter paginatedRequest.lastUpdateAt)
                }
                .paginated(paginatedRequest)
                .mapNotNull { it.toPlaylist() }
        }

    override suspend fun isPlaylistPossessedByUser(userId: UUID, playlistId: UUID): Boolean =
        suspendedTransaction {
            PlaylistTable
                .selectAll()
                .where {
                    (PlaylistTable.id eq playlistId) and (PlaylistTable.userId eq userId)
                }.count() > 0
        }
}