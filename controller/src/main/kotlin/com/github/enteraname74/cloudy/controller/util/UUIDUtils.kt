package com.github.enteraname74.cloudy.controller.util

import java.util.*

object UUIDUtils {
    fun fromString(string: String?): UUID? =
        runCatching {
            string?.let { UUID.fromString(string) }
        }.getOrNull()
}