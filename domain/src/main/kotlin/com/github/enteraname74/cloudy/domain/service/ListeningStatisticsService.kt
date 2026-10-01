package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.model.ListeningStatistics
import com.github.enteraname74.cloudy.domain.repository.ListeningStatisticsRepository
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import kotlin.uuid.Uuid

class ListeningStatisticsService(
    private val listeningStatisticsRepository: ListeningStatisticsRepository,
) {
    suspend fun upsertAll(statistics: List<ListeningStatistics>) {
        listeningStatisticsRepository.upsertAll(statistics)
    }

    suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest,
    ): List<ListeningStatistics> =
        listeningStatisticsRepository.getAllOfUser(
            userId = userId,
            paginatedRequest = paginatedRequest,
        )
}