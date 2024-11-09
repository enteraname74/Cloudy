package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.model.Album
import com.github.enteraname74.cloudy.localdb.table.AlbumTable
import com.github.enteraname74.cloudy.localdb.table.toAlbum
import com.github.enteraname74.cloudy.localdb.util.dbQuery
import com.github.enteraname74.cloudy.repository.datasource.AlbumDataSource
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

class AlbumDataSourceImpl : AlbumDataSource {
    override suspend fun getFromInformation(albumName: String, albumArtist: String, userId: UUID): Album? =
        dbQuery {
            AlbumTable
                .selectAll()
                .where {
                    (AlbumTable.name eq albumName) and (AlbumTable.artistName eq albumArtist) and (AlbumTable.userId eq userId)
                }.firstOrNull()
                ?.toAlbum()
        }

    override suspend fun upsert(album: Album): Album =
        dbQuery {
            AlbumTable.upsert {
                it[id] = album.id
                it[name] = album.name
                it[userId] = album.userId
                it[coverPath] = album.coverPath
                it[addedDate] = album.addedDate
                it[nbPlayed] = album.nbPlayed
                it[isInQuickAccess] = album.isInQuickAccess
                it[artistId] = album.artistId
                it[artistName] = album.artistName
                it[lastUpdateAt] = album.lastUpdateAt
            }

            AlbumTable
                .selectAll()
                .where { AlbumTable.id eq album.id }
                .first()
                .toAlbum()!!
        }

    override suspend fun upsertAll(albums: List<Album>) {
        dbQuery {
            AlbumTable.batchUpsert(albums) { album ->
                this[AlbumTable.id] = album.id
                this[AlbumTable.name] = album.name
                this[AlbumTable.userId] = album.userId
                this[AlbumTable.coverPath] = album.coverPath
                this[AlbumTable.addedDate] = album.addedDate
                this[AlbumTable.nbPlayed] = album.nbPlayed
                this[AlbumTable.isInQuickAccess] = album.isInQuickAccess
                this[AlbumTable.artistId] = album.artistId
                this[AlbumTable.artistName] = album.artistName
                this[AlbumTable.lastUpdateAt] = album.lastUpdateAt
            }
        }
    }

    override suspend fun getAllOfUser(userId: UUID): List<Album> =
        dbQuery {
            AlbumTable
                .selectAll()
                .where { AlbumTable.userId eq userId }
                .mapNotNull { it.toAlbum() }
        }

    override suspend fun deleteById(albumId: UUID) {
        dbQuery {
            AlbumTable.deleteWhere {
                AlbumTable.id eq albumId
            }
        }
    }

    override suspend fun allOfArtist(artistId: UUID): List<Album> =
        dbQuery {
            AlbumTable
                .selectAll()
                .where { AlbumTable.artistId eq  artistId }
                .mapNotNull { it.toAlbum() }
        }
}