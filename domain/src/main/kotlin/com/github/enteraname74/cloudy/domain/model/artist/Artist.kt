package com.github.enteraname74.cloudy.domain.model.artist

import com.github.enteraname74.cloudy.domain.model.UpdatableElement
import com.github.enteraname74.cloudy.domain.util.DateUtils
import kotlinx.serialization.Serializable
import kotlin.math.max
import kotlin.uuid.Uuid

@Serializable
data class Artist(
    val id: Uuid = Uuid.random(),
    val userId: Uuid,
    val name: String,
    val coverPath: String? = null,
    val addedDateMillis: Long = DateUtils.now(),
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = false,
    override val lastUpdateAtMillis: Long = DateUtils.now(),
) : UpdatableElement {
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

    fun merge(
        artistUpdate: ArtistUpdate,
    ): Artist =
        copy(
            name = name,
            nbPlayed = max(artistUpdate.nbPlayed, nbPlayed),
            isInQuickAccess = artistUpdate.isInQuickAccess,
        )
}