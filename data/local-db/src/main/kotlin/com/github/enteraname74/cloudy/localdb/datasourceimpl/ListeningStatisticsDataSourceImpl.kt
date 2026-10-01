package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.model.ListeningStatistics
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.localdb.table.ListeningStatisticsEntity
import com.github.enteraname74.cloudy.localdb.table.ListeningStatisticsTable
import com.github.enteraname74.cloudy.localdb.util.paginated
import com.github.enteraname74.cloudy.localdb.util.updatedAfter
import com.github.enteraname74.cloudy.localdb.util.workTransaction
import com.github.enteraname74.cloudy.repository.datasource.ListeningStatisticsDataSource
import org.jetbrains.exposed.v1.core.Case
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.jdbc.batchUpsert
import kotlin.uuid.Uuid

class ListeningStatisticsDataSourceImpl : ListeningStatisticsDataSource {
    override suspend fun upsertAll(statistics: List<ListeningStatistics>) {
        workTransaction {
            ListeningStatisticsTable.batchUpsert(
                statistics,
                ListeningStatisticsTable.id,
                onUpdate = { update ->
                    val incomingLastUpdate = insertValue(ListeningStatisticsTable.lastUpdateAt)
                    val incomingNbPlayed = insertValue(ListeningStatisticsTable.nbPlayed)
                    val incomingTimeListened = insertValue(ListeningStatisticsTable.timeListened)

                    update[ListeningStatisticsTable.lastUpdateAt] = Case()
                        .When(
                            ListeningStatisticsTable.lastUpdateAt greater incomingLastUpdate,
                            ListeningStatisticsTable.lastUpdateAt,
                        )
                        .Else(incomingLastUpdate)

                    update[ListeningStatisticsTable.nbPlayed] = Case()
                        .When(
                            ListeningStatisticsTable.nbPlayed greater incomingNbPlayed,
                            ListeningStatisticsTable.nbPlayed,
                        )
                        .Else(incomingNbPlayed)

                    update[ListeningStatisticsTable.timeListened] = Case()
                        .When(
                            ListeningStatisticsTable.timeListened.isNull(),
                            incomingTimeListened,
                        )
                        .When(
                            incomingTimeListened.isNull(),
                            ListeningStatisticsTable.timeListened,
                        )
                        .When(
                            ListeningStatisticsTable.timeListened greater incomingTimeListened,
                            ListeningStatisticsTable.timeListened,
                        )
                        .Else(incomingTimeListened)
                },
                shouldReturnGeneratedValues = false,
            ) { statistic ->
                this[ListeningStatisticsTable.id] = statistic.id
                this[ListeningStatisticsTable.userId] = statistic.userId
                this[ListeningStatisticsTable.nbPlayed] = statistic.nbPlayed
                this[ListeningStatisticsTable.timeListened] = statistic.timeListened?.inWholeMilliseconds
                this[ListeningStatisticsTable.month] = statistic.localMonthYear.month
                this[ListeningStatisticsTable.year] = statistic.localMonthYear.year
                this[ListeningStatisticsTable.lastUpdateAt] = statistic.lastUpdateAtMillis
                this[ListeningStatisticsTable.musicId] = statistic.musicId?.raw
                this[ListeningStatisticsTable.albumId] = statistic.albumId
                this[ListeningStatisticsTable.artistId] = statistic.artistId
                this[ListeningStatisticsTable.playlistId] = statistic.playlistId
            }
        }
    }

    override suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest,
    ): List<ListeningStatistics> =
        workTransaction {
            ListeningStatisticsEntity
                .find {
                    (ListeningStatisticsTable.userId eq userId) and
                        (ListeningStatisticsTable.lastUpdateAt updatedAfter paginatedRequest.lastUpdateAtMillis)
                }.paginated(paginatedRequest)
                .map { it.toListeningStats() }
        }
}
