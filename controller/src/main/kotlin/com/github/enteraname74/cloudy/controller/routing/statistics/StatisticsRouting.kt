package com.github.enteraname74.cloudy.controller.routing.statistics

import com.github.enteraname74.cloudy.config.plugin.authenticatedRoutes
import com.github.enteraname74.cloudy.controller.routing.statistics.routes.statisticsOfUser
import com.github.enteraname74.cloudy.controller.routing.statistics.routes.upsertStatistics
import io.ktor.server.routing.Routing

fun Routing.statisticsRouting() {
    authenticatedRoutes {
        statisticsOfUser()
        upsertStatistics()
    }
}