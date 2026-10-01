package com.github.enteraname74.cloudy.domain.ext

import kotlin.uuid.Uuid

fun String.toUuid(): Uuid? =
    Uuid.parseOrNull(this)
