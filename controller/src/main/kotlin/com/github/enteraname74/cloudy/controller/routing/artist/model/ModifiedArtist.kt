package com.github.enteraname74.cloudy.controller.routing.artist.model

import com.github.enteraname74.cloudy.domain.model.Artist
import com.github.enteraname74.cloudy.domain.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ModifiedArtist(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    var nbPlayed: Int = 0,
    var isInQuickAccess: Boolean = false,
)

fun Artist.fromModifiedArtist(modifiedArtist: ModifiedArtist): Artist =
    this.copy(
        id = modifiedArtist.id,
        name = modifiedArtist.name,
        nbPlayed = modifiedArtist.nbPlayed,
        isInQuickAccess = modifiedArtist.isInQuickAccess,
    )
