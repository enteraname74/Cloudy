package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.model.Music
import com.github.enteraname74.cloudy.localdb.table.MusicTable
import com.github.enteraname74.cloudy.localdb.table.toMusic
import com.github.enteraname74.cloudy.localdb.util.dbQuery
import com.github.enteraname74.cloudy.repository.datasource.MusicDataSource
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.upsert
import java.util.*

class MusicDataSourceImpl: MusicDataSource {
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
                it[lastUpdateAt] = music.lastUpdatedAt
            }
        }
    }

    override suspend fun getFromInfo(
        musicId: UUID,
        userId: UUID,
    ): Music? =
        dbQuery {
            MusicTable
                .selectAll()
                .where { (MusicTable.id eq musicId) and (MusicTable.userId eq userId) }
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

    override suspend fun getAllOfUser(userId: UUID): List<Music> =
        dbQuery {
            MusicTable
                .selectAll()
                .where { MusicTable.userId eq userId }
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

    override suspend fun allFromAlbum(albumId: UUID, userId: UUID): List<Music> =
        dbQuery {
            MusicTable
                .selectAll()
                .where { (MusicTable.albumId eq albumId) and (MusicTable.userId eq userId) }
                .mapNotNull { it.toMusic() }
        }

    override suspend fun allFromArtist(artistId: UUID, userId: UUID): List<Music> =
        dbQuery {
            MusicTable
                .selectAll()
                .where { (MusicTable.artistId eq artistId) and (MusicTable.userId eq userId) }
                .mapNotNull { it.toMusic() }
        }
}