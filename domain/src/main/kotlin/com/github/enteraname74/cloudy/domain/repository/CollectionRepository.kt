package com.github.enteraname74.cloudy.domain.repository

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.cover.Cover
import com.github.enteraname74.cloudy.domain.model.collection.Collection
import com.github.enteraname74.cloudy.domain.model.collection.CollectionWithElements
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import kotlin.uuid.Uuid

interface CollectionRepository {
    suspend fun getFromUser(collectionId: Uuid, userId: Uuid): Collection?
    suspend fun getWithElements(collectionId: Uuid): CollectionWithElements?
    suspend fun getFromCoverPath(coverPath: String): Collection?
    suspend fun getFromInformation(name: String, userId: Uuid): Collection?
    suspend fun upsert(
        collection: Collection,
        coversData: List<FileData?>,
        coverMode: Cover.Mode?,
    ): Collection

    suspend fun replaceElements(
        collectionId: Uuid,
        userId: Uuid,
        artistIds: List<Uuid>,
        albumIds: List<Uuid>,
    )

    suspend fun deleteAll(collectionIds: List<Uuid>, userId: Uuid)
    suspend fun deleteOfUser(userId: Uuid)
    suspend fun allOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest = PaginatedRequest(),
    ): List<CollectionWithElements>

    suspend fun isCollectionPossessedByUser(userId: Uuid, collectionId: Uuid): Boolean
    suspend fun getDeletedCollectionIds(idsToCheck: List<Uuid>, userId: Uuid): List<Uuid>
}
