package com.github.enteraname74.cloudy.domain.model.collection

import com.github.enteraname74.cloudy.domain.model.UpdatableElement
import com.github.enteraname74.cloudy.domain.model.cover.Cover
import com.github.enteraname74.cloudy.domain.util.DateUtils
import kotlinx.serialization.Serializable
import kotlin.math.max
import kotlin.uuid.Uuid

@Serializable
data class Collection(
    val id: Uuid = Uuid.random(),
    val userId: Uuid,
    val name: String,
    val cover: Cover?,
    val addedDateMillis: Long = DateUtils.now(),
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = false,
    override val lastUpdateAtMillis: Long = DateUtils.now(),
) : UpdatableElement {
    fun merge(collectionUpload: CollectionUpload): Collection = copy(
        name = collectionUpload.name,
        nbPlayed = max(nbPlayed, collectionUpload.nbPlayed),
        isInQuickAccess = collectionUpload.isInQuickAccess,
    )

    companion object {
        const val COVER_PATH: String = "collection/cover/"
    }
}
