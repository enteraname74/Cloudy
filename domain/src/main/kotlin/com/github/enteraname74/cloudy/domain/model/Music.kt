package com.github.enteraname74.cloudy.domain.model

import com.github.enteraname74.cloudy.domain.util.DateUtils
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.uuid.Uuid

@Serializable
data class Music(
    val fingerprint: String,
    val userId: Uuid,
    val name: String,
    val album: Album,
    val artists: List<Artist>,
    val path: String,
    val albumPosition: Int?,
    val coverPath: String?,
    val duration: Long,
    val addedDateMillis: Long,
    override val lastUpdateAtMillis: Long = DateUtils.now(),
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = false,
): UpdatableElement {
    companion object {
        fun buildLocalCoverPath(): String =
            "$COVER_PATH${UUID.randomUUID()}"

        const val COVER_PATH = "music/cover/"
    }
}
