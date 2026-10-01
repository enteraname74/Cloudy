package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.model.playlist.Playlist
import com.github.enteraname74.cloudy.domain.model.playlist.PlaylistWithMusics
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.localdb.table.PlaylistEntity
import com.github.enteraname74.cloudy.localdb.table.PlaylistTable
import com.github.enteraname74.cloudy.localdb.table.PlaylistTable.isFavorite
import com.github.enteraname74.cloudy.localdb.table.PlaylistTable.lastUpdateAt
import com.github.enteraname74.cloudy.localdb.util.paginated
import com.github.enteraname74.cloudy.localdb.util.updatedAfter
import com.github.enteraname74.cloudy.localdb.util.workTransaction
import com.github.enteraname74.cloudy.repository.datasource.MusicPlaylistDataSource
import com.github.enteraname74.cloudy.repository.datasource.PlaylistDataSource
import com.github.enteraname74.cloudy.repository.ext.getCoverName
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.isNotNull
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.update
import kotlin.uuid.Uuid

class PlaylistDataSourceImpl(
    private val musicPlaylistDataSource: MusicPlaylistDataSource,
) : PlaylistDataSource {
    override suspend fun getFromUser(
        playlistId: Uuid,
        userId: Uuid,
    ): Playlist? =
        workTransaction {
            PlaylistEntity.find {
                (PlaylistTable.userId eq userId) and (PlaylistTable.id eq playlistId)
            }.firstOrNull()?.toPlaylist()
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
            PlaylistTable.upsertAll(listOf(playlist))

            PlaylistEntity
                .findById(playlist.id)
                ?.toPlaylist()!!
        }

    override suspend fun upsertAll(playlists: List<Playlist>): List<Playlist> =
        workTransaction {
            PlaylistTable.upsertAll(playlists)

            val playlistIds = playlists.map { it.id }

            PlaylistEntity
                .find { PlaylistTable.id inList playlistIds }
                .map { it.toPlaylist() }
        }

    override suspend fun updateLastUpdatedAtField(
        playlistIds: List<Uuid>,
        updatedAt: Long,
    ) {
        workTransaction {
            PlaylistTable.update({ PlaylistTable.id inList playlistIds }) {
                it[PlaylistTable.lastUpdateAt] = updatedAt
            }
        }
    }

    override suspend fun getAllCoverNamesOfUser(userId: Uuid): List<String> =
        workTransaction {
            PlaylistTable
                .select(PlaylistTable.coverPath)
                .where { (PlaylistTable.userId eq userId) and PlaylistTable.coverPath.isNotNull() }
                .mapNotNull { it[PlaylistTable.coverPath]?.getCoverName(Playlist.COVER_PATH) }
                .distinct()
        }

    override suspend fun deleteAll(
        playlistIds: List<Uuid>,
        userId: Uuid,
    ) {
        workTransaction {
            PlaylistTable.deleteWhere {
                (id inList playlistIds) and (PlaylistTable.userId eq userId)
            }
        }
    }

    override suspend fun deleteOfUser(userId: Uuid) {
        workTransaction {
            PlaylistTable.deleteWhere {
                this.userId eq userId
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

    override suspend fun getDeletedPlaylistIds(
        idsToCheck: List<Uuid>,
        userId: Uuid,
    ): List<Uuid> = workTransaction {
        val existingIds: List<Uuid> = PlaylistTable
            .select(PlaylistTable.id)
            .where { (PlaylistTable.userId eq userId) and (PlaylistTable.id inList idsToCheck) }
            .mapNotNull { result ->
                result.getOrNull(PlaylistTable.id)?.value
            }

        idsToCheck - existingIds.toSet()
    }
}