package com.github.enteraname74.cloudy.controller.routing.soulsearching.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.routing.soulsearching.resource.SoulSearchingResource
import com.github.enteraname74.cloudy.domain.model.soulsearching.SoulSearchingSettings
import com.github.enteraname74.cloudy.domain.service.SoulSearchingSettingsService
import io.ktor.server.resources.get
import io.ktor.server.response.respondNullable
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.getSoulSearchingSettings() {
    val service by inject<SoulSearchingSettingsService>()

    get<SoulSearchingResource.Settings> {
        val userId: Uuid = getUserIdFromToken() ?: return@get missingTokenInformation()
        val settings: SoulSearchingSettings? = service.get(userId = userId)

        call.respondNullable(settings)
    }
}
