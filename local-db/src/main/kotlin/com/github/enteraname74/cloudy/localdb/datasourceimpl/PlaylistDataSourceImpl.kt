package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.model.Playlist
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.localdb.table.PlaylistTable
import com.github.enteraname74.cloudy.localdb.table.toPlaylist
import com.github.enteraname74.cloudy.localdb.util.dbQuery
import com.github.enteraname74.cloudy.localdb.util.paginated
import com.github.enteraname74.cloudy.localdb.util.updatedAfter
import com.github.enteraname74.cloudy.repository.datasource.PlaylistDataSource
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.upsert
import java.util.*

class PlaylistDataSourceImpl: PlaylistDataSource {
    override suspend fun getFromId(playlistId: UUID): Playlist? =
        dbQuery {
            PlaylistTable
                .selectAll()
                .where {
                    PlaylistTable.id eq playlistId
                }.firstOrNull()
                ?.toPlaylist()
        }

    override suspend fun getFromInformation(name: String, userId: UUID): Playlist? =
        dbQuery {
            PlaylistTable
                .selectAll()
                .where {
                    (PlaylistTable.name eq name) and (PlaylistTable.userId eq userId)
                }.firstOrNull()
                ?.toPlaylist()
        }

    override suspend fun upsert(playlist: Playlist): Playlist =
        dbQuery {
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

    override suspend fun deleteById(playlistId: UUID) {
        dbQuery {
            PlaylistTable.deleteWhere {
                PlaylistTable.id eq playlistId
            }
        }
    }

    override suspend fun allOfUser(userId: UUID, paginatedRequest: PaginatedRequest): List<Playlist> =
        dbQuery {
            PlaylistTable
                .selectAll()
                .where {
                    (PlaylistTable.userId eq userId) and
                            (PlaylistTable.lastUpdateAt updatedAfter paginatedRequest.lastUpdateAt)
                }
                .paginated(paginatedRequest)
                .mapNotNull { it.toPlaylist() }
        }

    override suspend fun isPlaylistPossessedByUser(userId: UUID, playlistId: UUID): Boolean =
        dbQuery {
            PlaylistTable
                .selectAll()
                .where {
                    (PlaylistTable.id eq playlistId) and (PlaylistTable.userId eq userId)
                }.count() > 0
        }
}