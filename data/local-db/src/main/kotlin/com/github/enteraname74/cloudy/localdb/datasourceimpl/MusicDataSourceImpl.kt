package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.model.Music
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.localdb.table.MusicArtistTable
import com.github.enteraname74.cloudy.localdb.table.MusicTable
import com.github.enteraname74.cloudy.localdb.table.toMusic
import com.github.enteraname74.cloudy.localdb.util.suspendedTransaction
import com.github.enteraname74.cloudy.localdb.util.paginated
import com.github.enteraname74.cloudy.localdb.util.updatedAfter
import com.github.enteraname74.cloudy.repository.datasource.MusicDataSource
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import java.util.*
import kotlin.collections.mapNotNull

class MusicDataSourceImpl : MusicDataSource {
    override suspend fun upsert(music: Music): Music =
        suspendedTransaction {
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
                it[fingerprint] = music.fingerprint
                it[path] = music.path
                it[lastUpdateAt] = music.lastUpdateAt
            }

            MusicTable
                .selectAll()
                .where { MusicTable.id eq music.id }
                .first()
                .toMusic()!!
        }

    override suspend fun upsertAll(musics: List<Music>) {
        suspendedTransaction {
            MusicTable.batchUpsert(musics) { music ->
                this[MusicTable.id] = music.id
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
                this[MusicTable.fingerprint] = music.fingerprint
                this[MusicTable.path] = music.path
                this[MusicTable.lastUpdateAt] = music.lastUpdateAt
            }
        }
    }

    override suspend fun getFromId(musicId: UUID): Music? =
        suspendedTransaction {
            MusicTable
                .selectAll()
                .where { MusicTable.id eq musicId }
                .firstOrNull()
                ?.toMusic()
        }

    override suspend fun getAll(ids: List<UUID>): List<Music> =
        suspendedTransaction {
            MusicTable
                .selectAll()
                .where { MusicTable.id inList ids }
                .mapNotNull { it.toMusic() }
        }

    override suspend fun deleteAll(ids: List<UUID>) {
        suspendedTransaction {
            MusicTable.deleteWhere {
                id inList ids
            }
        }
    }

    override suspend fun getAllOfUser(
        userId: UUID,
        paginatedRequest: PaginatedRequest,
    ): List<Music> =
        suspendedTransaction {
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
        suspendedTransaction {
            MusicTable
                .selectAll()
                .where { (MusicTable.id eq musicId) and (MusicTable.userId eq userId) }
                .count() > 0
        }

    override suspend fun getFromFingerprint(fingerprint: String, userId: UUID): Music? =
        suspendedTransaction {
            MusicTable
                .selectAll()
                .where { (MusicTable.fingerprint eq fingerprint) and (MusicTable.userId eq userId) }
                .firstOrNull()
                ?.toMusic()
        }

    override suspend fun allFromAlbum(albumId: UUID): List<Music> =
        suspendedTransaction {
            MusicTable
                .selectAll()
                .where { MusicTable.albumId eq albumId }
                .mapNotNull { it.toMusic() }
        }

    override suspend fun allFromArtist(artistId: UUID): List<Music> =
        suspendedTransaction {
            MusicTable.join(
                otherTable = MusicArtistTable,
                joinType = JoinType.INNER,
                onColumn = MusicTable.id,
                otherColumn = MusicArtistTable.musicId,
                additionalConstraint = { MusicArtistTable.artistId eq artistId }
            )
                .selectAll()
                .mapNotNull { it.toMusic() }
        }
}