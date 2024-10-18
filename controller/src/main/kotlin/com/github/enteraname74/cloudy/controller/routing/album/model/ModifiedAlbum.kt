package com.github.enteraname74.cloudy.controller.routing.album.model

import com.github.enteraname74.cloudy.domain.model.Album
import com.github.enteraname74.cloudy.domain.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class ModifiedAlbum(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val artistName: String,
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = true,
)

fun Album.fromModifiedAlbum(modifiedAlbum: ModifiedAlbum): Album =
    this.copy(
        id = modifiedAlbum.id,
        name = modifiedAlbum.name,
        artistName = modifiedAlbum.artistName,
        nbPlayed = modifiedAlbum.nbPlayed,
        isInQuickAccess = modifiedAlbum.isInQuickAccess,
    )
