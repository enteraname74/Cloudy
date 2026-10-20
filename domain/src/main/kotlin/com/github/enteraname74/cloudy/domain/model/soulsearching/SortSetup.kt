package com.github.enteraname74.cloudy.domain.model.soulsearching

import kotlinx.serialization.Serializable

@Serializable
data class SortSetup(
    val type: SortType,
    val direction: SortDirection,
)
