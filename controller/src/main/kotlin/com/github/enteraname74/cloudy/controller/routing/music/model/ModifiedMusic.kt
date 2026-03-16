package com.github.enteraname74.cloudy.controller.routing.music.model

import com.github.enteraname74.cloudy.domain.model.album.Album
import com.github.enteraname74.cloudy.domain.model.artist.Artist
import com.github.enteraname74.cloudy.domain.model.music.Music
import com.github.enteraname74.cloudy.domain.util.DateUtils
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

// TODO: Improve to take album and artists directly
@Serializable
data class ModifiedMusic(
    val id: String,
    val name: String,
    val album: String,
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = false,
    val artists: List<String>,
)

// TODO: Improve album/artists
fun Music.fromModifiedMusic(modifiedMusic: ModifiedMusic): Music =
    this.copy(
        fingerprint = modifiedMusic.id,
        name = modifiedMusic.name,
        album = Album(
            name = modifiedMusic.album,
            id = Uuid.random(),
            userId = userId,
            coverPath = null,
            addedDateMillis = DateUtils.now(),
            artist = Artist(
                id = Uuid.random(),
                userId = userId,
                name = "",
                coverPath = null,
                addedDateMillis = DateUtils.now(),
            ),
        ),
        nbPlayed = modifiedMusic.nbPlayed,
        isInQuickAccess = modifiedMusic.isInQuickAccess,
        lastUpdateAtMillis = DateUtils.now(),
    )
