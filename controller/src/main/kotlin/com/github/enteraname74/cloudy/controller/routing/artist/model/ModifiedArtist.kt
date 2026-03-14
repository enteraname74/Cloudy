package com.github.enteraname74.cloudy.controller.routing.artist.model

import com.github.enteraname74.cloudy.domain.model.Artist
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class ModifiedArtist(
    val id: Uuid,
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
