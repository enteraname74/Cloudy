package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.model.playlist.Playlist
import com.github.enteraname74.cloudy.domain.model.playlist.PlaylistWithMusics
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.localdb.table.PlaylistEntity
import com.github.enteraname74.cloudy.localdb.table.PlaylistTable
import com.github.enteraname74.cloudy.localdb.table.PlaylistTable.addedDate
import com.github.enteraname74.cloudy.localdb.table.PlaylistTable.coverPath
import com.github.enteraname74.cloudy.localdb.table.PlaylistTable.isFavorite
import com.github.enteraname74.cloudy.localdb.table.PlaylistTable.isInQuickAccess
import com.github.enteraname74.cloudy.localdb.table.PlaylistTable.lastUpdateAt
import com.github.enteraname74.cloudy.localdb.table.PlaylistTable.name
import com.github.enteraname74.cloudy.localdb.table.PlaylistTable.nbPlayed
import com.github.enteraname74.cloudy.localdb.table.PlaylistTable.userId
import com.github.enteraname74.cloudy.localdb.util.paginated
import com.github.enteraname74.cloudy.localdb.util.updatedAfter
import com.github.enteraname74.cloudy.localdb.util.workTransaction
import com.github.enteraname74.cloudy.repository.datasource.MusicPlaylistDataSource
import com.github.enteraname74.cloudy.repository.datasource.PlaylistDataSource
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.batchUpsert
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.upsert
import kotlin.uuid.Uuid

class PlaylistDataSourceImpl(
    private val musicPlaylistDataSource: MusicPlaylistDataSource,
) : PlaylistDataSource {
    override suspend fun getFromId(playlistId: Uuid): Playlist? =
        workTransaction {
            PlaylistEntity.findById(playlistId)?.toPlaylist()
        }

    override suspend fun getWithMusics(playlistId: Uuid): PlaylistWithMusics? =
        workTransaction {
            PlaylistEntity.findById(playlistId)?.toPlaylist()?.let { playlist ->
                PlaylistWithMusics(
                    playlist = playlist,
                    musicIds = musicPlaylistDataSource.getAllOfPlaylist(playlist.id).map { it.musicId },
                )
            }
        }

    override suspend fun getFromCoverPath(coverPath: String): Playlist? =
        workTransaction {
            PlaylistEntity
                .find { PlaylistTable.coverPath eq coverPath }
                .firstOrNull()
                ?.toPlaylist()
        }

    override suspend fun getFromInformation(name: String, userId: Uuid): Playlist? =
        workTransaction {
            PlaylistEntity
                .find {
                    (PlaylistTable.name eq name) and (PlaylistTable.userId eq userId)
                }
                .firstOrNull()
                ?.toPlaylist()
        }

    override suspend fun getFavorite(userId: Uuid): Playlist? =
        workTransaction {
            PlaylistEntity
                .find {
                    (isFavorite eq Op.TRUE) and (PlaylistTable.userId eq userId)
                }
                .firstOrNull()
                ?.toPlaylist()
        }

    override suspend fun upsert(playlist: Playlist): Playlist =
        workTransaction {
            PlaylistTable.upsert {
                it[id] = playlist.id
                it[userId] = playlist.userId
                it[name] = playlist.name
                it[coverPath] = playlist.coverPath
                it[isFavorite] = playlist.isFavorite
                it[addedDate] = playlist.addedDateMillis
                it[nbPlayed] = playlist.nbPlayed
                it[isInQuickAccess] = playlist.isInQuickAccess
                it[lastUpdateAt] = playlist.lastUpdateAtMillis
            }

            PlaylistEntity
                .findById(playlist.id)
                ?.toPlaylist()!!
        }

    override suspend fun upsertAll(playlists: List<Playlist>): List<Playlist> =
        workTransaction {
            PlaylistTable.batchUpsert(playlists) { playlist ->
                this[PlaylistTable.id] = playlist.id
                this[userId] = playlist.userId
                this[name] = playlist.name
                this[coverPath] = playlist.coverPath
                this[isFavorite] = playlist.isFavorite
                this[addedDate] = playlist.addedDateMillis
                this[nbPlayed] = playlist.nbPlayed
                this[isInQuickAccess] = playlist.isInQuickAccess
                this[lastUpdateAt] = playlist.lastUpdateAtMillis
            }

            val playlistIds = playlists.map { it.id }

            PlaylistEntity
                .find { PlaylistTable.id inList playlistIds }
                .map { it.toPlaylist() }
        }

    override suspend fun deleteById(playlistId: Uuid): Boolean =
        workTransaction {
            PlaylistTable.deleteWhere {
                id eq playlistId
            } > 0
        }

    override suspend fun deleteAll(playlistIds: List<Uuid>) {
        workTransaction {
            PlaylistTable.deleteWhere {
                id inList playlistIds
            }
        }
    }

    override suspend fun allOfUser(userId: Uuid, paginatedRequest: PaginatedRequest): List<PlaylistWithMusics> =
        workTransaction {
            PlaylistEntity
                .find {
                    (PlaylistTable.userId eq userId) and
                            (lastUpdateAt updatedAfter paginatedRequest.lastUpdateAtMillis)
                }
                .paginated(paginatedRequest)
                .map { playlistEntity ->
                    val playlist = playlistEntity.toPlaylist()
                    PlaylistWithMusics(
                        playlist = playlist,
                        musicIds = musicPlaylistDataSource.getAllOfPlaylist(playlist.id).map { it.musicId },
                    )
                }
        }

    override suspend fun isPlaylistPossessedByUser(userId: Uuid, playlistId: Uuid): Boolean =
        workTransaction {
            PlaylistEntity
                .find {
                    (PlaylistTable.id eq playlistId) and (PlaylistTable.userId eq userId)
                }.count() > 0
        }
}