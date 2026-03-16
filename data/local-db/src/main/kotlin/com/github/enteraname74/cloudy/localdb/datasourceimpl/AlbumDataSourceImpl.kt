package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.model.album.Album
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.localdb.table.AlbumEntity
import com.github.enteraname74.cloudy.localdb.table.AlbumTable
import com.github.enteraname74.cloudy.localdb.table.ArtistTable
import com.github.enteraname74.cloudy.localdb.table.MusicTable
import com.github.enteraname74.cloudy.localdb.util.paginated
import com.github.enteraname74.cloudy.localdb.util.updatedAfter
import com.github.enteraname74.cloudy.localdb.util.workTransaction
import com.github.enteraname74.cloudy.repository.datasource.AlbumDataSource
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.notInSubQuery
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import kotlin.uuid.Uuid

class AlbumDataSourceImpl : AlbumDataSource {
    override suspend fun getFromId(albumId: Uuid): Album? =
        workTransaction {
            AlbumEntity
                .findById(albumId)
                ?.toAlbum()
        }

    override suspend fun getFromCoverPath(coverPath: String): Album? =
        workTransaction {
            AlbumEntity
                .find { AlbumTable.coverPath eq coverPath }
                .firstOrNull()
                ?.toAlbum()
        }

    override suspend fun getAll(albumIds: List<Uuid>): List<Album> =
        workTransaction {
            AlbumEntity
                .find { AlbumTable.id inList albumIds }
                .map { it.toAlbum() }
        }

    override suspend fun getFromInformation(albumName: String, albumArtist: String, userId: Uuid): Album? =
        workTransaction {
            val query = AlbumTable.innerJoin(ArtistTable)
                .selectAll()
                .where {
                    (AlbumTable.name eq albumName) and
                            (ArtistTable.name eq albumArtist) and
                            (ArtistTable.userId eq userId) and
                            (AlbumTable.userId eq userId)
                }.withDistinct()

            AlbumEntity
                .wrapRows(query)
                .firstOrNull()
                ?.toAlbum()
        }

    override suspend fun getFromUser(
        albumId: Uuid,
        userId: Uuid
    ): Album? =
        workTransaction {
            AlbumEntity
                .find { (AlbumTable.id eq albumId) and (AlbumTable.userId eq userId) }
                .firstOrNull()
                ?.toAlbum()
        }

    override suspend fun upsert(album: Album): Album =
        workTransaction {
            AlbumTable.upsertAll(listOf(album))
            AlbumEntity.findById(album.id)!!.toAlbum()
        }

    override suspend fun upsertAll(albums: List<Album>) {
        workTransaction {
            AlbumTable.upsertAll(albums)
        }
    }

    override suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest,
    ): List<Album> =
        workTransaction {
            AlbumEntity
                .find {
                    (AlbumTable.userId eq userId) and
                            (AlbumTable.lastUpdateAt updatedAfter paginatedRequest.lastUpdateAtMillis)
                }
                .paginated(paginatedRequest)
                .map { it.toAlbum() }
        }

    override suspend fun deleteById(albumId: Uuid) {
        workTransaction {
            AlbumEntity.findById(albumId)?.delete()
        }
    }

    override suspend fun deleteAll(albumIds: List<Uuid>) {
        workTransaction {
            AlbumTable.deleteWhere {
                id inList albumIds
            }
        }
    }

    override suspend fun allOfArtist(artistId: Uuid): List<Album> =
        workTransaction {
            AlbumEntity
                .find { AlbumTable.artistId eq artistId }
                .map { it.toAlbum() }
        }

    override suspend fun isAlbumPossessedByUser(userId: Uuid, albumId: Uuid): Boolean =
        workTransaction {
            AlbumEntity
                .find {
                    (AlbumTable.id eq albumId) and (AlbumTable.userId eq userId)
                }.count() > 0
        }

    override suspend fun deleteAllEmpty() {
        workTransaction {
            AlbumTable.deleteWhere {
                this.id notInSubQuery MusicTable
                    .select(MusicTable.albumId)
            }
        }
    }
}