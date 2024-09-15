package com.github.enteraname74.cloudy.domain.util

import com.github.enteraname74.cloudy.domain.serializer.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class PaginatedRequest(
    @Serializable(with = LocalDateTimeSerializer::class)
    val lastUpdateAt: LocalDateTime? = null,
)
