package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.model.artist.Artist
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.localdb.table.ArtistEntity
import com.github.enteraname74.cloudy.localdb.table.ArtistTable
import com.github.enteraname74.cloudy.localdb.table.MusicArtistTable
import com.github.enteraname74.cloudy.localdb.util.paginated
import com.github.enteraname74.cloudy.localdb.util.updatedAfter
import com.github.enteraname74.cloudy.localdb.util.workTransaction
import com.github.enteraname74.cloudy.repository.datasource.ArtistDataSource
import com.github.enteraname74.cloudy.repository.ext.getCoverName
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.isNotNull
import org.jetbrains.exposed.v1.core.notExists
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import kotlin.uuid.Uuid

class ArtistDataSourceImpl : ArtistDataSource {
    override suspend fun getFromInformation(name: String, userId: Uuid): Artist? =
        workTransaction {
            ArtistEntity
                .find { (ArtistTable.name eq name) and (ArtistTable.userId eq userId) }
                .firstOrNull()
                ?.toArtist()
        }

    override suspend fun getFromId(artistId: Uuid): Artist? =
        workTransaction {
            ArtistEntity
                .findById(artistId)
                ?.toArtist()
        }

    override suspend fun getFromUser(
        artistId: Uuid,
        userId: Uuid
    ): Artist? =
        workTransaction {
            ArtistEntity
                .find { (ArtistTable.id eq artistId) and (ArtistTable.userId eq userId) }
                .firstOrNull()
                ?.toArtist()
        }

    override suspend fun getFromCoverPath(coverPath: String): Artist? =
        workTransaction {
            ArtistEntity
                .find { ArtistTable.coverPath eq coverPath }
                .firstOrNull()
                ?.toArtist()
        }

    override suspend fun isArtistPossessedByUser(userId: Uuid, artistId: Uuid): Boolean =
        workTransaction {
            ArtistEntity
                .find { (ArtistTable.id eq artistId) and (ArtistTable.userId eq userId) }
                .count() > 0
        }

    override suspend fun upsert(artist: Artist): Artist =
        workTransaction {
            ArtistTable.upsertAll(listOf(artist))
            ArtistEntity.findById(artist.id)!!.toArtist()
        }

    override suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest,
    ): List<Artist> =
        workTransaction {
            ArtistEntity
                .find {
                    (ArtistTable.userId eq userId) and
                        (ArtistTable.lastUpdateAt updatedAfter paginatedRequest.lastUpdateAtMillis)
                }
                .paginated(paginatedRequest)
                .map { it.toArtist() }
        }

    override suspend fun getAllCoverNamesOfUser(userId: Uuid): List<String> =
        workTransaction {
            ArtistTable
                .select(ArtistTable.coverPath)
                .where { (ArtistTable.userId eq userId) and ArtistTable.coverPath.isNotNull() }
                .mapNotNull { it[ArtistTable.coverPath]?.getCoverName(Artist.COVER_PATH) }
                .distinct()
        }

    override suspend fun deleteAll(artistIds: List<Uuid>) {
        workTransaction {
            ArtistTable.deleteWhere {
                id inList artistIds
            }
        }
    }

    override suspend fun deleteOfUser(userId: Uuid) {
        workTransaction {
            ArtistTable.deleteWhere {
                this.userId eq userId
            }
        }
    }

    override suspend fun deleteAllEmpty() {
        workTransaction {
            ArtistTable.deleteWhere {
                notExists(
                    MusicArtistTable
                        .selectAll()
                        .where { MusicArtistTable.artistId eq ArtistTable.id }
                )
            }
        }
    }
}