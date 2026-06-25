package com.github.enteraname74.cloudy.controller.routing.music.model

import kotlinx.serialization.Serializable

@Serializable
data class FetchFromUrlBody(
    val url: String,
)
