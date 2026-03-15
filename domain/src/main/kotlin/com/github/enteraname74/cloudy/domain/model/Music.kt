package com.github.enteraname74.cloudy.domain.model

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
    val coverPath: String?,
    val duration: Long,
    val addedDateMillis: Long,
    override val lastUpdateAtMillis: Long = DateUtils.now(),
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = false,
): UpdatableElement {

    fun merge(
        musicUpload: MusicUpload,
        artists: List<Artist>,
        album: Album,
    ): Music =
        copy(
            name = musicUpload.name,
            album = album,
            artists = artists,
            albumPosition = musicUpload.albumPosition,
            duration = musicUpload.duration,
            nbPlayed = musicUpload.nbPlayed,
            isInQuickAccess = musicUpload.isInQuickAccess,
        )

    companion object {
        fun buildLocalCoverPath(): String =
            "$COVER_PATH${UUID.randomUUID()}"

        const val COVER_PATH = "music/cover/"
    }
}

@Serializable
data class MusicUpload(
    val name: String,
    val albumUpload: AlbumUpload,
    val artists: List<ArtistUpload>,
    val albumPosition: Int?,
    val duration: Long,
    val nbPlayed: Int,
    val isInQuickAccess: Boolean,
) {
    fun toNewMusic(
        userId: Uuid,
        artists: List<Artist>,
        album: Album,
        fingerprint: String,
        path: String,
    ): Music =
        Music(
            fingerprint = fingerprint,
            userId = userId,
            name = name,
            album = album,
            artists = artists,
            path = path,
            albumPosition = albumPosition,
            coverPath = null,
            duration = duration,
            addedDateMillis = DateUtils.now(),
        )
}
