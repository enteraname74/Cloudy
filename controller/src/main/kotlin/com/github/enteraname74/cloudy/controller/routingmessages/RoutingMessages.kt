package com.github.enteraname74.cloudy.controller.routingmessages

import com.github.enteraname74.cloudy.controller.routingmessages.impl.EnRoutingMessages
import com.github.enteraname74.cloudy.controller.routingmessages.impl.FrRoutingMessages
import kotlin.uuid.Uuid

interface RoutingMessages {
    val USERNAME_TAKEN: String
    val WRONG_INFORMATION: String
    val MISSING_USER_INFORMATION: String
    val CANNOT_CREATE_USER: String
    val CANNOT_FIND_USER: String
    val NOT_AN_ADMIN: String
    val INVALID_INSCRIPTION_CODE: String
    val MISSING_PERMISSION_FOR_DELETION: String
    val USER_DELETED: String

    val NO_FILE_DATA: String
    val USER_MAX_STORAGE_REACHED: String
    val FILE_TOO_HEAVY: String
    val FILE_NOT_FOUND: String
    val IMAGE_NOT_FOUND: String

    val SONGS_DELETED: String
    val GIVEN_FILE_IS_NOT_A_MUSIC_FILE: String
    val SONG_NOT_POSSESSED_BY_USER: String
    val CANNOT_SAVE_SONG: String
    val CANNOT_UPDATE_SONG: String

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

    fun playlistNotPossessedByUser(playlistId: Uuid): String

    val MISSING_TOKEN_INFORMATION: String
    val NOT_A_REFRESH_TOKEN: String

    val WRONG_ID: String
    val WRONG_BODY_DATA: String

    companion object {
        fun fromLocale(locale: AppLocale): RoutingMessages =
            when (locale) {
                AppLocale.Fr -> FrRoutingMessages
                AppLocale.En -> EnRoutingMessages
            }
    }
}