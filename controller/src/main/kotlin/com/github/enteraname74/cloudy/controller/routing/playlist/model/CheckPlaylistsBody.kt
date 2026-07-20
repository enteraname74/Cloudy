package com.github.enteraname74.cloudy.controller.routing.playlist.model

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class CheckPlaylistsBody(
    val ids: List<Uuid>,
)
