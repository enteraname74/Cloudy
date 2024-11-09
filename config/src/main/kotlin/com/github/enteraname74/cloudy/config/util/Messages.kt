package com.github.enteraname74.cloudy.config.util

internal object Messages {
    object Auth {
        const val TOKEN_ERROR = "Token is missing, not valid or has expired."
    }

    object StatusPage {
        const val INTERNAL_SERVER_ERROR = "There was an internal server error."
        const val UNKNOWN_ROUTE = "Unknown route."

        fun internalServerErrorWithCause(cause: String): String =
            "$INTERNAL_SERVER_ERROR\nCause: $cause"
    }
}