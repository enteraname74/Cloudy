package com.github.enteraname74.cloudy.domain.model

import com.github.enteraname74.cloudy.domain.util.DateUtils
import kotlinx.serialization.Serializable
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
}
