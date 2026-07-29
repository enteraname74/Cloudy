package com.github.enteraname74.cloudy.domain.routingmessages

import com.github.enteraname74.cloudy.domain.routingmessages.impl.EnRoutingMessages
import com.github.enteraname74.cloudy.domain.routingmessages.impl.FrRoutingMessages
import kotlin.uuid.Uuid

interface RoutingMessages {
    val USERNAME_TAKEN: String
    val WRONG_INFORMATION: String
    val INVALID_INFORMATION: String
    val MISSING_USER_INFORMATION: String
    val CANNOT_CREATE_USER: String
    val CANNOT_FIND_USER: String
    val NOT_AN_ADMIN: String
    val INVALID_INSCRIPTION_CODE: String
    val MISSING_PERMISSION_FOR_DELETION: String
    val USER_DELETED: String

    val INSCRIPTION_CODE_NOT_FOUND: String

    val NO_FILE_DATA: String
    val USER_MAX_STORAGE_REACHED: String
    val FILE_TOO_HEAVY: String
    val FILE_NOT_FOUND: String
    val IMAGE_NOT_FOUND: String

    val SONGS_DELETED: String
    val GIVEN_FILE_IS_NOT_A_MUSIC_FILE: String
    val SONG_NOT_POSSESSED_BY_USER: String
    val CANNOT_SAVE_SONG: String
    fun cannotUpdateSong(songId: String): String

    fun songNotPossessedByUser(musicId: String): String

    val ARTISTS_DELETED: String
    val ARTIST_NOT_POSSESSED_BY_USER: String

    fun artistNotPossessedByUser(artistId: Uuid): String

    val ALBUMS_DELETED: String
    val ALBUM_NOT_POSSESSED_BY_USER: String

    fun albumNotPossessedByUser(albumId: Uuid): String

    val PLAYLIST_NOT_FOUND: String
    val PLAYLISTS_DELETED: String
    val PLAYLIST_NOT_POSSESSED_BY_USER: String
    val PLAYLIST_ALREADY_EXISTING: String

    val CANNOT_SAVE_PLAYLIST: String

    fun playlistNotPossessedByUser(playlistId: Uuid): String

    val MISSING_TOKEN_INFORMATION: String
    val NOT_A_REFRESH_TOKEN: String

    val WRONG_ID: String
    val WRONG_BODY_DATA: String

    val USER_ALREADY_IN_PLAYED_LIST: String
    val PLAYED_LIST_NOT_FOUND: String
    val PLAYED_LIST_NOT_FOUND_OR_NOT_IN_LIST: String
    val NOT_OWNER_OF_PLAYED_LIST: String
    val NO_PERMISSION_TO_REMOVE_USER_IN_PLAYED_LIST: String
    val MISING_DEVICE_ID: String

    fun internalServerError(error: String): String

    companion object {
        fun fromLocale(locale: AppLocale): RoutingMessages =
            when (locale) {
                AppLocale.Fr -> FrRoutingMessages
                AppLocale.En -> EnRoutingMessages
            }
    }
}