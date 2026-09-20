package com.github.enteraname74.cloudy.controller.routing.statistics.routes

import com.github.enteraname74.cloudy.controller.routing.statistics.resource.StatisticsResource
import com.github.enteraname74.cloudy.domain.model.ListeningStatistics
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
        val statistics: List<ListeningStatistics> = call.receive()
        service.upsertAll(statistics)
        call.respond(HttpStatusCode.OK)
    }
}