package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.model.Music
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.localdb.table.AlbumTable.id
import com.github.enteraname74.cloudy.localdb.table.MusicTable
import com.github.enteraname74.cloudy.localdb.table.toMusic
import com.github.enteraname74.cloudy.localdb.util.dbQuery
import com.github.enteraname74.cloudy.localdb.util.paginated
import com.github.enteraname74.cloudy.localdb.util.updatedAfter
import com.github.enteraname74.cloudy.repository.datasource.MusicDataSource
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

class MusicDataSourceImpl : MusicDataSource {
    override suspend fun upsert(music: Music) {
        dbQuery {
            MusicTable.upsert {
                it[id] = music.id
                it[name] = music.name
                it[userId] = music.userId
                it[coverPath] = music.coverPath
                it[album] = music.album
                it[artist] = music.artist
                it[duration] = music.duration
                it[addedDate] = music.addedDate
                it[nbPlayed] = music.nbPlayed
                it[isInQuickAccess] = music.isInQuickAccess
                it[albumId] = music.albumId
                it[artistId] = music.artistId
                it[fingerprint] = music.fingerprint
                it[path] = music.path
                it[lastUpdateAt] = music.lastUpdateAt
            }
        }
    }

    override suspend fun upsertAll(musics: List<Music>) {
        dbQuery {
            MusicTable.batchUpsert(musics) { music ->
                this[id] = music.id
                this[MusicTable.name] = music.name
                this[MusicTable.userId] = music.userId
                this[MusicTable.coverPath] = music.coverPath
                this[MusicTable.album] = music.album
                this[MusicTable.artist] = music.artist
                this[MusicTable.duration] = music.duration
                this[MusicTable.addedDate] = music.addedDate
                this[MusicTable.nbPlayed] = music.nbPlayed
                this[MusicTable.isInQuickAccess] = music.isInQuickAccess
                this[MusicTable.albumId] = music.albumId
                this[MusicTable.artistId] = music.artistId
                this[MusicTable.fingerprint] = music.fingerprint
                this[MusicTable.path] = music.path
                this[MusicTable.lastUpdateAt] = music.lastUpdateAt
            }
        }
    }

    override suspend fun getFromId(musicId: UUID): Music? =
        dbQuery {
            MusicTable
                .selectAll()
                .where { MusicTable.id eq musicId }
                .firstOrNull()
                ?.toMusic()
        }

    override suspend fun deleteById(musicId: UUID) {
        dbQuery {
            MusicTable.deleteWhere {
                MusicTable.id eq musicId
            }
        }
    }

    override suspend fun getAllOfUser(
        userId: UUID,
        paginatedRequest: PaginatedRequest,
    ): List<Music> =
        dbQuery {
            MusicTable
                .selectAll()
                .where {
                    (MusicTable.userId eq userId) and
                            (MusicTable.lastUpdateAt updatedAfter paginatedRequest.lastUpdateAt)
                }
                .paginated(paginatedRequest)
                .mapNotNull { it.toMusic() }
        }

    override suspend fun isMusicPossessedByUser(userId: UUID, musicId: UUID): Boolean =
        dbQuery {
            MusicTable
                .selectAll()
                .where { (MusicTable.id eq musicId) and (MusicTable.userId eq userId) }
                .count() > 0
        }

    override suspend fun doesMusicExists(fingerprint: String, userId: UUID): Boolean =
        dbQuery {
            MusicTable
                .selectAll()
                .where { (MusicTable.fingerprint eq fingerprint) and (MusicTable.userId eq userId) }
                .count() > 0
        }

    override suspend fun allFromAlbum(albumId: UUID): List<Music> =
        dbQuery {
            MusicTable
                .selectAll()
                .where { MusicTable.albumId eq albumId }
                .mapNotNull { it.toMusic() }
        }

    override suspend fun allFromArtist(artistId: UUID): List<Music> =
        dbQuery {
            MusicTable
                .selectAll()
                .where { MusicTable.artistId eq artistId }
                .mapNotNull { it.toMusic() }
        }
}