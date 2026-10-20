package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.collection.Collection
import com.github.enteraname74.cloudy.domain.model.collection.CollectionUpload
import com.github.enteraname74.cloudy.domain.model.collection.CollectionWithElements
import com.github.enteraname74.cloudy.domain.repository.CollectionRepository
import com.github.enteraname74.cloudy.domain.repository.CoverRepository
import com.github.enteraname74.cloudy.domain.usecase.collection.UploadCollectionUseCase
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import kotlin.uuid.Uuid

class CollectionService(
    private val collectionRepository: CollectionRepository,
    private val uploadCollectionUseCase: UploadCollectionUseCase,
    private val coverRepository: CoverRepository,
) {
    suspend fun getFromCoverPath(coverPath: String): Collection? =
        collectionRepository.getFromCoverPath(coverPath)

    suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest,
    ): List<CollectionWithElements> = collectionRepository.allOfUser(userId, paginatedRequest)

    suspend fun isCollectionPossessedByUser(userId: Uuid, collectionId: Uuid): Boolean =
        collectionRepository.isCollectionPossessedByUser(userId, collectionId)

    suspend fun upload(
        collectionUpload: CollectionUpload,
        coversData: List<FileData?>,
        userId: Uuid,
    ): CloudyResult<CollectionWithElements> {
        val result = uploadCollectionUseCase(collectionUpload, coversData, userId)
        if (result is CloudyResult.Success) coverRepository.deletedUnusedCovers(userId)
        return result
    }

    suspend fun deleteAll(collectionIds: List<Uuid>, userId: Uuid) {
        collectionRepository.deleteAll(collectionIds, userId)
        coverRepository.deletedUnusedCovers(userId)
    }

    suspend fun getDeletedCollectionIds(idsToCheck: List<Uuid>, userId: Uuid): List<Uuid> =
        collectionRepository.getDeletedCollectionIds(idsToCheck, userId)
}
