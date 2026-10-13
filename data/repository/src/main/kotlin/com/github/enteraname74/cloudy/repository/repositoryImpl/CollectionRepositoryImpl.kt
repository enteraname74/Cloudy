package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.collection.Collection
import com.github.enteraname74.cloudy.domain.model.collection.CollectionWithElements
import com.github.enteraname74.cloudy.domain.repository.CollectionRepository
import com.github.enteraname74.cloudy.domain.util.DateUtils
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.repository.datasource.CollectionDataSource
import com.github.enteraname74.cloudy.repository.datasource.CoverDataSource
import com.github.enteraname74.cloudy.repository.ext.getCoverName
import kotlin.uuid.Uuid

class CollectionRepositoryImpl(
    private val collectionDataSource: CollectionDataSource,
    private val coverDataSource: CoverDataSource,
) : CollectionRepository {
    override suspend fun getFromUser(collectionId: Uuid, userId: Uuid): Collection? =
        collectionDataSource.getFromUser(collectionId, userId)

    override suspend fun getWithElements(collectionId: Uuid): CollectionWithElements? =
        collectionDataSource.getWithElements(collectionId)

    override suspend fun getFromCoverPath(coverPath: String): Collection? =
        collectionDataSource.getFromCoverPath(coverPath)

    override suspend fun getFromInformation(name: String, userId: Uuid): Collection? =
        collectionDataSource.getFromInformation(name, userId)

    override suspend fun upsert(collection: Collection, coverData: FileData?): Collection {
        val savedId = coverData?.let { cover ->
            collection.coverPath.getCoverName(Collection.COVER_PATH)?.let { previousName ->
                coverDataSource.delete(name = previousName, userId = collection.userId)
            }
            coverDataSource.save(userId = collection.userId, data = cover)
        }
        return collectionDataSource.upsert(
            collection.copy(
                coverPath = savedId?.let { "${Collection.COVER_PATH}$it" } ?: collection.coverPath,
                lastUpdateAtMillis = DateUtils.now(),
            )
        )
    }

    override suspend fun replaceElements(
        collectionId: Uuid,
        userId: Uuid,
        artistIds: List<Uuid>,
        albumIds: List<Uuid>,
    ) = collectionDataSource.replaceElements(collectionId, userId, artistIds, albumIds)

    override suspend fun deleteAll(collectionIds: List<Uuid>, userId: Uuid) =
        collectionDataSource.deleteAll(collectionIds, userId)

    override suspend fun deleteOfUser(userId: Uuid) = collectionDataSource.deleteOfUser(userId)

    override suspend fun allOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest,
    ): List<CollectionWithElements> = collectionDataSource.allOfUser(userId, paginatedRequest)

    override suspend fun isCollectionPossessedByUser(userId: Uuid, collectionId: Uuid): Boolean =
        collectionDataSource.isCollectionPossessedByUser(userId, collectionId)

    override suspend fun getDeletedCollectionIds(idsToCheck: List<Uuid>, userId: Uuid): List<Uuid> =
        collectionDataSource.getDeletedCollectionIds(idsToCheck, userId)
}
