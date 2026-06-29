package com.github.enteraname74.cloudy.controller.routing.playlist.model

import com.github.enteraname74.cloudy.domain.model.playlist.PlaylistUpload
import kotlinx.serialization.Serializable

@Serializable
data class UploadPlaylistBody(
    val playlists: List<PlaylistUpload>,
)
