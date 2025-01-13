package com.github.enteraname74.cloudy.domain.model

import java.util.UUID

data class MusicArtist(
    val musicId: UUID,
    val artistId: UUID
) {
    val id: String
        get() = "$musicId$artistId"
}
