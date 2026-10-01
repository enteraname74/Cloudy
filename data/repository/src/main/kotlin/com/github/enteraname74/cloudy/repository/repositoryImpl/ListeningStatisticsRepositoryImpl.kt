package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.ListeningStatistics
import com.github.enteraname74.cloudy.domain.repository.ListeningStatisticsRepository
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.repository.datasource.ListeningStatisticsDataSource
import kotlin.uuid.Uuid

class ListeningStatisticsRepositoryImpl(
    private val dataSource: ListeningStatisticsDataSource,
) : ListeningStatisticsRepository {
    override suspend fun upsertAll(statistics: List<ListeningStatistics>) {
        dataSource.upsertAll(statistics)
    }

    override suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest,
    ): List<ListeningStatistics> =
        dataSource.getAllOfUser(
            userId = userId,
            paginatedRequest = paginatedRequest,
        )
}