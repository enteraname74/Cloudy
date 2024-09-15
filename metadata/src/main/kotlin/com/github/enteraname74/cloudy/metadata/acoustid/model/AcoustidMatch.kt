package com.github.enteraname74.cloudy.metadata.acoustid.model

import kotlinx.serialization.Serializable

@Serializable
internal data class AcoustidMatch(
    val recordings: List<AcoustidRecording>,
    val score: Float,
)
