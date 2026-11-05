package com.github.enteraname74.cloudy.metadata.acoustid.model

import kotlinx.serialization.Serializable

@Serializable
internal data class AcoustidLookupRequestResult(
    val status: String,
    // Acoustid sends no results when the request fails.
    val results: List<AcoustidMatch> = emptyList(),
    val error: AcoustidError? = null,
) {
    val isSuccess: Boolean
        get() = status == "ok"
}

@Serializable
internal data class AcoustidError(
    val message: String? = null,
)
