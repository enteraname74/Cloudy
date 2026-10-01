package com.github.enteraname74.cloudy.domain.util

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedRequest(
    val lastUpdateAtMillis: Long? = null,
    val page: Int? = null,
    val limitPerPage: Int? = null,
)
