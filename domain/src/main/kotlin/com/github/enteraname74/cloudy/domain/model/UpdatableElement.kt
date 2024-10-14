package com.github.enteraname74.cloudy.domain.model

import java.time.LocalDateTime

interface UpdatableElement {
    val lastUpdateAt: LocalDateTime
}