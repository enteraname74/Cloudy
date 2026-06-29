package com.github.enteraname74.cloudy.domain.model

import com.github.enteraname74.cloudy.domain.util.DateUtils
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class Playlist(
    val id: Uuid,
    val userId: Uuid,
    val name: String,
    val coverPath: String?,
    val isFavorite: Boolean = false,
    val addedDateMillis: Long = DateUtils.now(),
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = false,
    override val lastUpdateAtMillis: Long = DateUtils.now(),
): UpdatableElement {
    companion object {
        const val COVER_PATH = "playlist/cover/"
    }
}
