package com.github.enteraname74.cloudy.domain.util

import kotlin.time.Clock

object DateUtils {
    fun now(): Long = Clock.System.now().toEpochMilliseconds()
}