package com.github.enteraname74.cloudy.controller.routing.soulsearching

import com.github.enteraname74.cloudy.config.plugin.authenticatedRoutes
import com.github.enteraname74.cloudy.controller.routing.soulsearching.routes.getSoulSearchingSettings
import com.github.enteraname74.cloudy.controller.routing.soulsearching.routes.upsertSoulSearchingSettings
import io.ktor.server.routing.Routing

fun Routing.soulSearchingRouting() {
    authenticatedRoutes {
        getSoulSearchingSettings()
        upsertSoulSearchingSettings()
    }
}
