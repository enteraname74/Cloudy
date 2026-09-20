package com.github.enteraname74.cloudy.controller.routing.statistics.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.routing.statistics.resource.StatisticsResource
import com.github.enteraname74.cloudy.domain.model.ListeningStatistics
import com.github.enteraname74.cloudy.domain.service.ListeningStatisticsService
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.statisticsOfUser() {
    val service by inject<ListeningStatisticsService>()

    get<StatisticsResource.OfUser> { resource ->
        val userId: Uuid = getUserIdFromToken() ?: return@get missingTokenInformation()

        val data: List<ListeningStatistics> = service.getAllOfUser(
            userId = userId,
            paginatedRequest = resource.toPaginatedRequest(),
        )

        call.respond(data)
    }
}