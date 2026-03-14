package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.model.MusicPlaylist
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.localdb.table.MusicPlaylistEntity
import com.github.enteraname74.cloudy.localdb.table.MusicPlaylistTable
import com.github.enteraname74.cloudy.localdb.util.paginated
import com.github.enteraname74.cloudy.localdb.util.updatedAfter
import com.github.enteraname74.cloudy.localdb.util.workTransaction
import com.github.enteraname74.cloudy.repository.datasource.MusicPlaylistDataSource
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.batchUpsert
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.upsert
import kotlin.uuid.Uuid

class MusicPlaylistDataSourceImpl : MusicPlaylistDataSource {
    override suspend fun upsert(musicPlaylist: MusicPlaylist) {
        workTransaction {
            MusicPlaylistTable.upsert {
                it[id] = musicPlaylist.id
                it[musicId] = musicPlaylist.musicId
                it[playlistId] = musicPlaylist.playlistId
                it[userId] = musicPlaylist.userId
                it[lastUpdateAt] = musicPlaylist.lastUpdateAtMillis
            }
        }
    }

    override suspend fun upsertAll(musicPlaylists: List<MusicPlaylist>) {
        workTransaction {
            MusicPlaylistTable.batchUpsert(musicPlaylists) { musicPlaylist ->
                this[MusicPlaylistTable.id] = musicPlaylist.id
                this[MusicPlaylistTable.musicId] = musicPlaylist.musicId
                this[MusicPlaylistTable.playlistId] = musicPlaylist.playlistId
                this[MusicPlaylistTable.userId] = musicPlaylist.userId
                this[MusicPlaylistTable.lastUpdateAt] = musicPlaylist.lastUpdateAtMillis
            }
        }
    }

    override suspend fun delete(musicPlaylist: MusicPlaylist) {
        workTransaction {
            MusicPlaylistTable.deleteWhere {
                id eq musicPlaylist.id
            }
        }
    }

    override suspend fun deleteAll(ids: List<String>) {
        workTransaction {
            MusicPlaylistTable.deleteWhere {
                id inList ids
            }
        }
    }

    override suspend fun getAllOfPlaylist(playlistId: Uuid): List<MusicPlaylist> =
        workTransaction {
            MusicPlaylistEntity
                .find { MusicPlaylistTable.playlistId eq playlistId }
                .map { it.toMusicPlaylist() }
        }

    override suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest
    ): List<MusicPlaylist> =
        workTransaction {
            MusicPlaylistEntity
                .find {
                    (MusicPlaylistTable.userId eq userId) and
                            (MusicPlaylistTable.lastUpdateAt updatedAfter paginatedRequest.lastUpdateAtMillis)
                }
                .paginated(paginatedRequest)
                .map { it.toMusicPlaylist() }
        }
}