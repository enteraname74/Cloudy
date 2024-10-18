package com.github.enteraname74.cloudy.controller.util

object ServerUtil {
    val HOST: String = System.getenv("HOST") ?: "0.0.0.0"
    val PORT: Int = System.getenv("PORT")?.toInt() ?: 8080

    fun buildRoute(value: String): String =
        "http://$HOST:$PORT/$value"


    object Keys {
        const val LAST_UPDATE_AT_KEY: String = "lastUpdateAt"
        const val PAGE_KEY: String = "page"
        const val MAX_PER_PAGE: String = "maxPerPage"
    }
}