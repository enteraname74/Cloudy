package com.github.enteraname74.cloudy.domain.model.collection

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class CollectionUpload(
    val id: Uuid?,
    val name: String,
    val nbPlayed: Int,
    val isInQuickAccess: Boolean,
    val artistIds: List<Uuid>,
    val albumIds: List<Uuid>,
) {
    fun isValid(): Boolean = name.isNotBlank() && nbPlayed >= 0

    fun toNewCollection(userId: Uuid): Collection = Collection(
        userId = userId,
        name = name,
        coverPath = null,
        nbPlayed = nbPlayed,
        isInQuickAccess = isInQuickAccess,
    )
}
