package com.github.enteraname74.cloudy.controller.util

import java.util.*

object UUIDUtils {
    fun fromString(string: String?): UUID? =
        try {
            string?.let { UUID.fromString(string) }
        } catch (_: Exception) {
            null
        }
}