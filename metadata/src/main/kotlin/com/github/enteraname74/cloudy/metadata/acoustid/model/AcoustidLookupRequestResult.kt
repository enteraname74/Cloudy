package com.github.enteraname74.cloudy.metadata.acoustid.model

import kotlinx.serialization.Serializable

@Serializable
internal data class AcoustidLookupRequestResult(
    val results: List<AcoustidMatch>
)
