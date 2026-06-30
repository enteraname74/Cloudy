package com.github.enteraname74.cloudy.controller.routing.player.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.routing.player.resource.PlayerResource
import com.github.enteraname74.cloudy.domain.service.PlayerService
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.getAllOfUser() {
    val playerService by inject<PlayerService>()

    get<PlayerResource> {
        val userId = getUserIdFromToken() ?: return@get missingTokenInformation()

        call.respond(playerService.getAllWhereUserIsIn(userId))
    }
}