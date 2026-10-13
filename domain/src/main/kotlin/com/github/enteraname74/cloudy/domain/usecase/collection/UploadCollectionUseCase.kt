package com.github.enteraname74.cloudy.domain.usecase.collection

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.collection.Collection
import com.github.enteraname74.cloudy.domain.model.collection.CollectionUpload
import com.github.enteraname74.cloudy.domain.model.collection.CollectionWithElements
import com.github.enteraname74.cloudy.domain.repository.CollectionRepository
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.domain.util.toCloudyResult
import kotlin.uuid.Uuid

class UploadCollectionUseCase(
    private val collectionRepository: CollectionRepository,
) {
    suspend operator fun invoke(
        collectionUpload: CollectionUpload,
        coverData: FileData?,
        userId: Uuid,
    ): CloudyResult<CollectionWithElements> {
        if (!collectionUpload.isValid()) return CloudyResult.Error()

        val existingCollection: Collection? = collectionUpload.id?.let {
            collectionRepository.getFromUser(collectionId = it, userId = userId)
        } ?: collectionRepository.getFromInformation(
            name = collectionUpload.name,
            userId = userId,
        )
        val savedCollection = collectionRepository.upsert(
            collection = existingCollection?.merge(collectionUpload)
                ?: collectionUpload.toNewCollection(userId),
            coverData = coverData,
        )
        collectionRepository.replaceElements(
            collectionId = savedCollection.id,
            userId = userId,
            artistIds = collectionUpload.artistIds,
            albumIds = collectionUpload.albumIds,
        )
        return collectionRepository.getWithElements(savedCollection.id).toCloudyResult()
    }
}
