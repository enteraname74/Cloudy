package com.github.enteraname74.cloudy.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class LocalMonthYear(
    val month: Int,
    val year: Int,
)
