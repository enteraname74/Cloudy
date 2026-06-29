package com.github.enteraname74.cloudy.controller.routing.album.model

import com.github.enteraname74.cloudy.domain.model.Album
import com.github.enteraname74.cloudy.domain.model.Artist
import com.github.enteraname74.cloudy.domain.util.DateUtils
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid


// TODO: Improve to take artist directly
@Serializable
data class ModifiedAlbum(
    val id: Uuid,
    val name: String,
    val artistName: String,
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = true,
)

// TODO: Improve artist handling
fun Album.fromModifiedAlbum(modifiedAlbum: ModifiedAlbum): Album =
    this.copy(
        id = modifiedAlbum.id,
        name = modifiedAlbum.name,
        artist = Artist(
            name = modifiedAlbum.artistName,
            id = Uuid.random(),
            userId = userId,
            coverPath = null,
            addedDateMillis = DateUtils.now(),
        ),
        nbPlayed = modifiedAlbum.nbPlayed,
        isInQuickAccess = modifiedAlbum.isInQuickAccess,
    )
