package com.github.enteraname74.cloudy.domain.model.artist

import com.github.enteraname74.cloudy.domain.util.DateUtils
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class ArtistUpload(
    val name: String,
    val nbPlayed: Int,
    val isInQuickAccess: Boolean,
) {
    fun toNewArtist(
        userId: Uuid
    ): Artist =
        Artist(
            id = Uuid.random(),
            userId = userId,
            name = name,
            coverPath = null,
            addedDateMillis = DateUtils.now(),
            nbPlayed = nbPlayed,
            isInQuickAccess = isInQuickAccess,
            lastUpdateAtMillis = DateUtils.now(),
        )
}