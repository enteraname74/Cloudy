package com.github.enteraname74.cloudy.metadata.acoustid.model

import kotlinx.serialization.Serializable

@Serializable
internal data class AcoustidMatch(
    val recordings: List<AcoustidRecording> = emptyList(),
    // Confidence that the fingerprint matches the audio, between 0 and 1.
    val score: Float,
)
