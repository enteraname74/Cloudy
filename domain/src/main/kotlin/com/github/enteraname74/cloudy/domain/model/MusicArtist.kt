package com.github.enteraname74.cloudy.domain.model

import com.github.enteraname74.cloudy.domain.util.DateUtils
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class MusicArtist(
    val musicId: String,
    val artistId: Uuid,
    val userId: Uuid,
    override val lastUpdateAtMillis: Long = DateUtils.now(),
): UpdatableElement {
    val id: String
        get() = "$musicId$artistId"
}
