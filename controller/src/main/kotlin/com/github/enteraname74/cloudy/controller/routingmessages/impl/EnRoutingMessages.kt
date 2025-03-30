package com.github.enteraname74.cloudy.controller.routingmessages.impl

import com.github.enteraname74.cloudy.controller.routingmessages.RoutingMessages
import java.util.UUID

object EnRoutingMessages: RoutingMessages {
    override val USERNAME_TAKEN: String = "The username is already taken."
    override val WRONG_INFORMATION: String = "The given information are incorrect."
    override val MISSING_USER_INFORMATION: String = "Missing username or password."
    override val CANNOT_CREATE_USER: String = "Cannot create user."
    override val CANNOT_FIND_USER: String = "Cannot find the user."
    override val NOT_AN_ADMIN: String = "The user is not an admin."
    override val INVALID_INSCRIPTION_CODE: String = "The given inscription code is invalid."
    override val MISSING_PERMISSION_FOR_DELETION: String = "The user cannot delete the requested profile."
    override val USER_DELETED: String = "The user was deleted."

    override val NO_FILE_DATA: String = "No file data found."
    override val USER_MAX_STORAGE_REACHED: String = "The user has no storage space left."
    override val FILE_TOO_HEAVY: String = "The file is too heavy."
    override val FILE_NOT_FOUND: String = "Couldn't find the requested music file."
    override val IMAGE_NOT_FOUND: String = "Couldn't find the requested image file."

    override val SONGS_DELETED: String = "Songs have been deleted."
    override val GIVEN_FILE_IS_NOT_A_MUSIC_FILE: String = "Given file is not a music file."
    override val SONG_NOT_POSSESSED_BY_USER: String = "This song is not possessed by the user."
    override val CANNOT_SAVE_SONG: String = "Cannot save the given song."
    override val CANNOT_UPDATE_SONG: String = "Cannot update the song."

    override fun songNotPossessedByUser(musicId: UUID): String =
        "The song with id: $musicId is not possessed by the user."

    override val ARTISTS_DELETED: String = "Artists have been deleted."
    override val ARTIST_NOT_POSSESSED_BY_USER: String = "This artist is not possessed by the user."

    override fun artistNotPossessedByUser(artistId: UUID): String =
        "The artist with id: $artistId is not possessed by the user."

    override val ALBUMS_DELETED: String = "Albums have been deleted."
    override val ALBUM_NOT_POSSESSED_BY_USER: String = "This album is not possessed by the user."

    override fun albumNotPossessedByUser(albumId: UUID): String =
        "The album with id: $albumId is not possessed by the user."

    override val PLAYLIST_NOT_FOUND: String = "This playlist does not exist."
    override val PLAYLISTS_DELETED: String = "Playlists have been deleted"
    override val PLAYLIST_NOT_POSSESSED_BY_USER: String = "This playlist is not possessed by the user."
    override val PLAYLIST_ALREADY_EXISTING: String = "This playlist already exists."

    override fun playlistNotPossessedByUser(playlistId: UUID): String =
        "The album with id: $playlistId is not possessed by the user."

    override val MISSING_TOKEN_INFORMATION: String = "Missing information from token."
    override val NOT_A_REFRESH_TOKEN: String = "The given token is not a refresh token."

    override val WRONG_ID: String = "The given id is incorrect."
    override val WRONG_BODY_DATA: String = "The given data is incorrect."
}