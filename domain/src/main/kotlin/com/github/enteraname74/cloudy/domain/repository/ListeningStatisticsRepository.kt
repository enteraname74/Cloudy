package com.github.enteraname74.cloudy.domain.repository

import com.github.enteraname74.cloudy.domain.model.ListeningStatistics
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import kotlin.uuid.Uuid

interface ListeningStatisticsRepository {
    suspend fun upsertAll(statistics: List<ListeningStatistics>)

    suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest,
    ): List<ListeningStatistics>
}