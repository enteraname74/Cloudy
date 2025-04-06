package com.github.enteraname74.cloudy.domain.ext

import java.util.UUID

fun String.toUUID(): UUID? =
    runCatching {
        UUID.fromString(this)
    }.getOrNull()