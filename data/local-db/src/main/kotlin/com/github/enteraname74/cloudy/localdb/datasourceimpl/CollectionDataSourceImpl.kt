package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.model.collection.Collection
import com.github.enteraname74.cloudy.domain.model.collection.CollectionWithElements
import com.github.enteraname74.cloudy.domain.util.DateUtils
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.localdb.table.AlbumTable
import com.github.enteraname74.cloudy.localdb.table.ArtistTable
import com.github.enteraname74.cloudy.localdb.table.CollectionAlbumTable
import com.github.enteraname74.cloudy.localdb.table.CollectionArtistTable
import com.github.enteraname74.cloudy.localdb.table.CollectionEntity
import com.github.enteraname74.cloudy.localdb.table.CollectionTable
import com.github.enteraname74.cloudy.localdb.util.paginated
import com.github.enteraname74.cloudy.localdb.util.updatedAfter
import com.github.enteraname74.cloudy.localdb.util.workTransaction
import com.github.enteraname74.cloudy.repository.datasource.CollectionDataSource
import com.github.enteraname74.cloudy.repository.ext.getCoverName
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.isNotNull
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.update
import kotlin.uuid.Uuid

class CollectionDataSourceImpl : CollectionDataSource {
    override suspend fun getFromUser(collectionId: Uuid, userId: Uuid): Collection? = workTransaction {
        CollectionEntity.find {
            (CollectionTable.id eq collectionId) and (CollectionTable.userId eq userId)
        }.firstOrNull()?.toCollection()
    }

    override suspend fun getWithElements(collectionId: Uuid): CollectionWithElements? = workTransaction {
        CollectionEntity.findById(collectionId)?.toCollection()?.let(::withElements)
    }

    override suspend fun getFromCoverPath(coverPath: String): Collection? = workTransaction {
        CollectionEntity.find {
            (CollectionTable.coverUrl0 eq coverPath) or
                (CollectionTable.coverUrl1 eq coverPath) or
                (CollectionTable.coverUrl2 eq coverPath) or
                (CollectionTable.coverUrl3 eq coverPath)
        }
            .firstOrNull()
            ?.toCollection()
    }

    override suspend fun getFromInformation(name: String, userId: Uuid): Collection? = workTransaction {
        CollectionEntity.find {
            (CollectionTable.name eq name) and (CollectionTable.userId eq userId)
        }.firstOrNull()?.toCollection()
    }

    override suspend fun upsert(collection: Collection): Collection = workTransaction {
        CollectionTable.upsertAll(listOf(collection))
        CollectionEntity.findById(collection.id)!!.toCollection()
    }

    override suspend fun replaceElements(
        collectionId: Uuid,
        userId: Uuid,
        artistIds: List<Uuid>,
        albumIds: List<Uuid>,
    ) {
        workTransaction {
            val safeArtistIds = if (artistIds.isEmpty()) emptyList() else ArtistTable
                .select(ArtistTable.id)
                .where { (ArtistTable.userId eq userId) and (ArtistTable.id inList artistIds.distinct()) }
                .map { it[ArtistTable.id].value }
            val safeAlbumIds = if (albumIds.isEmpty()) emptyList() else AlbumTable
                .select(AlbumTable.id)
                .where { (AlbumTable.userId eq userId) and (AlbumTable.id inList albumIds.distinct()) }
                .map { it[AlbumTable.id].value }

            CollectionArtistTable.deleteWhere { CollectionArtistTable.collectionId eq collectionId }
            CollectionAlbumTable.deleteWhere { CollectionAlbumTable.collectionId eq collectionId }

            val updatedAt = DateUtils.now()
            CollectionArtistTable.upsertAll(collectionId, userId, safeArtistIds, updatedAt)
            CollectionAlbumTable.upsertAll(collectionId, userId, safeAlbumIds, updatedAt)
            CollectionTable.update({
                (CollectionTable.id eq collectionId) and (CollectionTable.userId eq userId)
            }) {
                it[lastUpdateAt] = updatedAt
            }
        }
    }

    override suspend fun getAllCoverNamesOfUser(userId: Uuid): List<String> = workTransaction {
        val coverColumns = listOf(
            CollectionTable.coverUrl0,
            CollectionTable.coverUrl1,
            CollectionTable.coverUrl2,
            CollectionTable.coverUrl3,
        )
        CollectionTable.select(coverColumns)
            .where {
                (CollectionTable.userId eq userId) and
                    (CollectionTable.coverUrl0.isNotNull() or
                        CollectionTable.coverUrl1.isNotNull() or
                        CollectionTable.coverUrl2.isNotNull() or
                        CollectionTable.coverUrl3.isNotNull())
            }
            .flatMap { row -> coverColumns.mapNotNull { row[it]?.getCoverName(Collection.COVER_PATH) } }
            .distinct()
    }

    override suspend fun deleteAll(collectionIds: List<Uuid>, userId: Uuid) {
        if (collectionIds.isEmpty()) return
        workTransaction {
            CollectionTable.deleteWhere {
                (id inList collectionIds) and (CollectionTable.userId eq userId)
            }
        }
    }

    override suspend fun deleteOfUser(userId: Uuid) {
        workTransaction {
            CollectionTable.deleteWhere { CollectionTable.userId eq userId }
        }
    }

    override suspend fun allOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest,
    ): List<CollectionWithElements> = workTransaction {
        CollectionEntity.find {
            (CollectionTable.userId eq userId) and
                (CollectionTable.lastUpdateAt updatedAfter paginatedRequest.lastUpdateAtMillis)
        }.paginated(paginatedRequest).map { withElements(it.toCollection()) }
    }

    override suspend fun isCollectionPossessedByUser(userId: Uuid, collectionId: Uuid): Boolean = workTransaction {
        CollectionEntity.find {
            (CollectionTable.id eq collectionId) and (CollectionTable.userId eq userId)
        }.count() > 0
    }

    override suspend fun getDeletedCollectionIds(idsToCheck: List<Uuid>, userId: Uuid): List<Uuid> {
        if (idsToCheck.isEmpty()) return emptyList()
        return workTransaction {
            val existingIds = CollectionTable.select(CollectionTable.id)
                .where { (CollectionTable.userId eq userId) and (CollectionTable.id inList idsToCheck) }
                .map { it[CollectionTable.id].value }
            idsToCheck - existingIds.toSet()
        }
    }

    private fun withElements(collection: Collection): CollectionWithElements = CollectionWithElements(
        collection = collection,
        artistIds = CollectionArtistTable.select(CollectionArtistTable.artistId)
            .where { CollectionArtistTable.collectionId eq collection.id }
            .map { it[CollectionArtistTable.artistId].value },
        albumIds = CollectionAlbumTable.select(CollectionAlbumTable.albumId)
            .where { CollectionAlbumTable.collectionId eq collection.id }
            .map { it[CollectionAlbumTable.albumId].value },
    )
}
