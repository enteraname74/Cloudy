package com.github.enteraname74.cloudy.domain.model

import kotlinx.serialization.Serializable

/**
 * Custom meta data of a music, provided by a user.
 */
@Serializable
data class CustomMusicMetadata(
    val name: String? = null,
    val album: String? = null,
    val artists: List<String> = emptyList(),
    val duration: Long? = null,
)