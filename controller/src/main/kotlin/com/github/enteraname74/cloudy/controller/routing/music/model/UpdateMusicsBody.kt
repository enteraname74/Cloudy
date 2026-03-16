package com.github.enteraname74.cloudy.controller.routing.music.model

import com.github.enteraname74.cloudy.domain.model.music.MusicUpdate
import kotlinx.serialization.Serializable

@Serializable
data class UpdateMusicsBody(
    val musics: List<MusicUpdate>,
)
