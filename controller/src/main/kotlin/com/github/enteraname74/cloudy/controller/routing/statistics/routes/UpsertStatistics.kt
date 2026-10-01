package com.github.enteraname74.cloudy.controller.routing.statistics.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.routing.statistics.model.UpsertListeningStatistics
import com.github.enteraname74.cloudy.controller.routing.statistics.resource.StatisticsResource
import com.github.enteraname74.cloudy.domain.service.ListeningStatisticsService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject

fun Route.upsertStatistics() {
    val service by inject<ListeningStatisticsService>()

    post<StatisticsResource> {
        val userId = getUserIdFromToken() ?: return@post missingTokenInformation()
        val statistics: List<UpsertListeningStatistics> = call.receive()
        service.upsertAll(
            statistics = statistics.map { it.toListeningStatistics(userId) }
        )
        call.respond(HttpStatusCode.OK)
    }
}