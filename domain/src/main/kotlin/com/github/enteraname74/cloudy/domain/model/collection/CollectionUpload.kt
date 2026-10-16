package com.github.enteraname74.cloudy.domain.model.collection

import com.github.enteraname74.cloudy.domain.model.cover.Cover
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
    val coverMode: Cover.Mode,
) {
    fun isValid(): Boolean = name.isNotBlank() && nbPlayed >= 0

    fun toNewCollection(userId: Uuid): Collection = Collection(
        userId = userId,
        name = name,
        cover = null,
        nbPlayed = nbPlayed,
        isInQuickAccess = isInQuickAccess,
    )
}
