package com.github.enteraname74.cloudy.controller.util

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
        const val SONG_DELETED = "This song has been deleted."
        const val SONG_UPDATED = "This song has been updated."
        const val SONG_NOT_POSSESSED_BY_USER = "This song is not possessed by the user."
    }

    object Artist {
        const val ARTIST_UPDATED = "This artist has been updated."
        const val ARTIST_DELETED = "This artist has been deleted."
        const val ARTIST_NOT_POSSESSED_BY_USER = "This artist is not possessed by the user."
    }

    object Generic {
        const val MISSING_TOKEN_INFORMATION = "Missing information from token."
        const val WRONG_ID = "The given id is incorrect."
    }
}