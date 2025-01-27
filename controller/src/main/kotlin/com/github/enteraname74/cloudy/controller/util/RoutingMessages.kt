package com.github.enteraname74.cloudy.controller.util

import java.util.UUID

object RoutingMessages {
    object User {
        const val USERNAME_TAKEN = "The username is already taken."
        const val WRONG_INFORMATION = "The given information are incorrect."
        const val MISSING_INFORMATION = "Missing username or password."
        const val CANNOT_CREATE_USER = "Cannot create user."
        const val CANNOT_FIND_USER = "Cannot find the user."
    }

    object Music {
        const val NO_FILE_DATA = "No file data found."
        const val USER_MAX_STORAGE_REACHED = "The user has no storage space left."
        const val FILE_TOO_HEAVY = "The file is too heavy."
        const val FILE_SAVED = "File saved."
        const val FILE_NOT_FOUND = "Couldn't find the music file."
        const val SONGS_DELETED = "Songs have been deleted."
        const val SONG_NOT_POSSESSED_BY_USER = "This song is not possessed by the user."

        fun songNotPossessedByUser(musicId: UUID): String =
            "The song with id: $musicId is not possessed by the user."
    }

    object Artist {
        const val ARTISTS_DELETED = "Artists have been deleted."
        const val ARTIST_NOT_POSSESSED_BY_USER = "This artist is not possessed by the user."

        fun artistNotPossessedByUser(artistId: UUID): String =
            "The artist with id: $artistId is not possessed by the user."
    }

    object Album {
        const val ALBUMS_DELETED = "Albums have been deleted."
        const val ALBUM_NOT_POSSESSED_BY_USER = "This album is not possessed by the user."

        fun albumNotPossessedByUser(albumId: UUID): String =
            "The album with id: $albumId is not possessed by the user."
    }

    object Playlist {
        const val PLAYLISTS_DELETED = "Playlists have been deleted"
        const val PLAYLIST_NOT_POSSESSED_BY_USER = "This playlist is not possessed by the user."

        fun playlistNotPossessedByUser(playlistId: UUID): String =
            "The album with id: $playlistId is not possessed by the user."
    }

    object Auth {
        const val MISSING_TOKEN_INFORMATION = "Missing information from token."
        const val NOT_A_REFRESH_TOKEN = "The given token is not a refresh token."
    }

    object Generic {

        const val WRONG_ID = "The given id is incorrect."
    }
}