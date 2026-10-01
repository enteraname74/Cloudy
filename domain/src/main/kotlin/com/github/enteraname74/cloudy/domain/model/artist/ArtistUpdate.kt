package com.github.enteraname74.cloudy.domain.model.artist

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class ArtistUpdate(
    val id: Uuid? = null,
    val name: String,
    val nbPlayed: Int,
    val isInQuickAccess: Boolean
) {
    fun isValid(): Boolean =
        name.isNotBlank() && nbPlayed >= 0

    fun toNewArtist(
        userId: Uuid
    ): Artist =
        Artist(
            userId = userId,
            name = name,
            nbPlayed = nbPlayed,
            isInQuickAccess = isInQuickAccess,
        )
}
