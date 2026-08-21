package com.github.enteraname74.cloudy.domain.model.music

import com.github.enteraname74.cloudy.domain.model.UpdatableElement
import com.github.enteraname74.cloudy.domain.model.album.Album
import com.github.enteraname74.cloudy.domain.model.artist.Artist
import com.github.enteraname74.cloudy.domain.util.DateUtils
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.uuid.Uuid

@Serializable
data class Music(
    val fingerprint: String,
    val userId: Uuid,
    val name: String,
    val album: Album,
    val artists: List<Artist>,
    val path: String,
    val albumPosition: Int?,
    val coverPath: String = buildLocalCoverPath(),
    val duration: Long,
    val addedDateMillis: Long,
    override val lastUpdateAtMillis: Long = DateUtils.now(),
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = false,
    val scope: Scope,
): UpdatableElement {

    enum class Scope {
        User,
        SharedPlayedList
    }

    fun merge(
        musicUploadSpec: MusicUploadSpec,
        artists: List<Artist>,
        album: Album,
    ): Music =
        copy(
            name = musicUploadSpec.name,
            album = album,
            artists = artists,
            albumPosition = musicUploadSpec.albumPosition,
            duration = musicUploadSpec.duration,
            nbPlayed = musicUploadSpec.nbPlayed,
            isInQuickAccess = musicUploadSpec.isInQuickAccess,
        )

    fun merge(
        musicUpdate: MusicUpdate,
        artists: List<Artist>,
        album: Album,
    ): Music =
        copy(
            name = musicUpdate.name,
            album = album,
            artists = artists,
            albumPosition = musicUpdate.albumPosition,
            nbPlayed = musicUpdate.nbPlayed,
            isInQuickAccess = musicUpdate.isInQuickAccess,
        )

    companion object {
        fun buildLocalCoverPath(): String =
            "$COVER_PATH${UUID.randomUUID()}"

        const val COVER_PATH = "music/cover/"
    }
}

