package com.github.enteraname74.cloudy.controller.routing.music.model

import com.github.enteraname74.cloudy.domain.model.music.MusicId
import kotlinx.serialization.Serializable

@Serializable
data class CheckMusicsBody(
    val ids: List<MusicId>,
)
