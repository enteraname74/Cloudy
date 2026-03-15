package com.github.enteraname74.cloudy.domain.model

import com.github.enteraname74.cloudy.domain.util.DateUtils
import kotlinx.serialization.Serializable
import kotlin.math.max
import kotlin.uuid.Uuid

@Serializable
data class Artist(
    val id: Uuid,
    val userId: Uuid,
    val name: String,
    val coverPath: String?,
    var addedDateMillis: Long,
    var nbPlayed: Int = 0,
    var isInQuickAccess: Boolean = false,
    override val lastUpdateAtMillis: Long = DateUtils.now(),
): UpdatableElement {
    companion object {
        const val COVER_PATH = "artist/cover/"
    }

    fun merge(
        artistUpload: ArtistUpload,
    ): Artist =
        copy(
            name = artistUpload.name,
            nbPlayed = max(artistUpload.nbPlayed, nbPlayed),
            isInQuickAccess = artistUpload.isInQuickAccess,
        )
}

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