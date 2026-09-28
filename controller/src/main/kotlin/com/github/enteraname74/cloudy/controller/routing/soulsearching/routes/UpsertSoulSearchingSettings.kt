package com.github.enteraname74.cloudy.controller.routing.soulsearching.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.routing.soulsearching.resource.SoulSearchingResource
import com.github.enteraname74.cloudy.domain.model.soulsearching.SoulSearchingSettings
import com.github.enteraname74.cloudy.domain.service.SoulSearchingSettingsService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.upsertSoulSearchingSettings() {
    val service by inject<SoulSearchingSettingsService>()

    post<SoulSearchingResource.Settings> {
        val userId: Uuid = getUserIdFromToken() ?: return@post missingTokenInformation()
        val settings: SoulSearchingSettings = call.receive()

        service.upsert(
            userId = userId,
            settings = settings,
        )
        call.respond(HttpStatusCode.OK)
    }
}
