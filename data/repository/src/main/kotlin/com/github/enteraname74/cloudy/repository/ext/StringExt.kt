package com.github.enteraname74.cloudy.repository.ext

fun String?.getCoverName(base: String): String? =
    this?.takeIf { it.startsWith(base) }?.split('/')?.lastOrNull()