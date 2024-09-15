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
    }

    object Generic {
        const val MISSING_TOKEN_INFORMATION = "Missing information from token."
        const val WRONG_ID = "The given id is incorrect."
    }
}